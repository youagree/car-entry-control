
package ru.unit_techno.car_entry_control.config;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.spi.schema.contexts.ModelContext.inputParam;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelReference;
import springfox.documentation.schema.ResolvedTypes;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order
public class PageableForSwagger implements OperationBuilderPlugin {

    private static final String QUERY_PARAM = "query";
    private final TypeNameExtractor nameExtractor;
    private final TypeResolver resolver;
    private final ResolvedType pageableType;
    private final ResolvedType specificationType;

    @Autowired
    public PageableForSwagger(TypeNameExtractor nameExtractor, TypeResolver resolver) {
        this.nameExtractor = nameExtractor;
        this.resolver = resolver;
        this.pageableType = resolver.resolve(Pageable.class);
        this.specificationType = resolver.resolve(Specification.class);
    }

    @SneakyThrows
    @Override
    public void apply(OperationContext context) {
        List<ResolvedMethodParameter> methodParameters = context.getParameters();

        for (ResolvedMethodParameter methodParameter : methodParameters) {
            ResolvedType methodParameterType = methodParameter.getParameterType();

            if (pageableType.equals(methodParameterType)) {
                addPageableParameters(context, methodParameter);
            }
        }
    }

    private void addPageableParameters(OperationContext context, ResolvedMethodParameter methodParameter) throws IllegalAccessException, NoSuchFieldException {
        Function<ResolvedType, ? extends ModelReference> factory = getResolvedTypeFunction(context, methodParameter);

        ModelReference intModel = factory.apply(resolver.resolve(Integer.TYPE));
        ModelReference stringModel = factory.apply(resolver.resolve(List.class, String.class));

        List<Parameter> swaggerParameters = newArrayList();

        swaggerParameters.add(new ParameterBuilder()
                .parameterType(QUERY_PARAM)
                .name("page")
                .modelRef(intModel)
                .description("Results page you want to retrieve (0..N)").build());
        swaggerParameters.add(new ParameterBuilder()
                .parameterType(QUERY_PARAM)
                .name("size")
                .modelRef(intModel)
                .description("Number of records per page").build());
        swaggerParameters.add(new ParameterBuilder()
                .parameterType(QUERY_PARAM)
                .name("sort")
                .modelRef(stringModel)
                .allowMultiple(true)
                .description("Sorting criteria in the format: property(,asc|desc). "
                        + "Default sort order is ascending. "
                        + "Multiple sort criteria are supported.")
                .build());

        final OperationBuilder operationBuilder = context.operationBuilder();
        operationBuilder.parameters(swaggerParameters);

        Set<String> toDelete = Sets.newLinkedHashSet(Arrays
                .asList("offset", "pageNumber", "pageSize", "paged", "sort.sorted", "sort.unsorted", "unpaged"));
        final Field field = operationBuilder.getClass().getDeclaredField("parameters");
        field.setAccessible(true);
        @SuppressWarnings("unchecked") final List<Parameter> list = (List<Parameter>) field.get(operationBuilder);
        field.set(operationBuilder,
                list.stream().filter(p -> !toDelete.contains(p.getName())).collect(Collectors.toList()));
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }

    private Function<ResolvedType, ? extends ModelReference> getResolvedTypeFunction(OperationContext context,
                                                                                     ResolvedMethodParameter methodParameter) {
        ParameterContext parameterContext = new ParameterContext(methodParameter,
                new ParameterBuilder(),
                context.getDocumentationContext(),
                context.getGenericsNamingStrategy(),
                context);

        return createModelRefFactory(parameterContext);
    }

    private Function<ResolvedType, ? extends ModelReference> createModelRefFactory(ParameterContext context) {
        ModelContext modelContext = inputParam(
                context.getGroupName(),
                context.resolvedMethodParameter().getParameterType(),
                context.getDocumentationType(),
                context.getAlternateTypeProvider(),
                context.getGenericNamingStrategy(),
                context.getIgnorableParameterTypes());
        return ResolvedTypes.modelRefFactory(modelContext, nameExtractor);
    }
}