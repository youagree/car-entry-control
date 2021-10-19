
package ru.unit_techno.car_entry_control.config;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.spi.schema.contexts.ModelContext.inputParam;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.NotNull;
import net.kaczmarzyk.spring.data.jpa.domain.Null;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Order
@Component
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
            } else if (specificationType.getErasedType().equals(methodParameterType.getErasedType())) {
                addSpecificationParameters(context, methodParameter);
            }
        }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }

    private void addSpecificationParameters(OperationContext context, ResolvedMethodParameter methodParameter) {
        List<Spec> specs = SpecExtractor.extractSpecList(methodParameter.getAnnotations());

        Function<ResolvedType, ? extends ModelReference> factory = getResolvedTypeFunction(context, methodParameter);

        Class<?> domainClass = methodParameter
                .getParameterType()
                .getTypeParameters()
                .stream()
                .map(ResolvedType::getErasedType)
                .findFirst()
                .orElse(null);

        Map<String, List<Spec>> paramNameSpecMap = getMapOfSpecIsClassifiedByParamName(specs);

        List<Parameter> swaggerParameters = paramNameSpecMap.entrySet()
                .stream()
                .map(paramNameSpecEntry -> buildSwaggerParameter(paramNameSpecEntry, domainClass, factory))
                .collect(Collectors.toList());

        context.operationBuilder().parameters(swaggerParameters);
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

    private Map<String, List<Spec>> getMapOfSpecIsClassifiedByParamName(List<Spec> specs) {
        Map<String, List<Spec>> mapOfSpec = new HashMap<>();
        specs.stream()
                .filter(spec -> StringUtils.isNotEmpty(spec.path()))
                .forEach(spec -> getNamesOfParam(spec)
                        .forEach(nameOfParam -> {
                            if (mapOfSpec.containsKey(nameOfParam)) {
                                List<Spec> updatedListOfSpec = new ArrayList<>(mapOfSpec.get(nameOfParam));
                                updatedListOfSpec.add(spec);
                                mapOfSpec.put(nameOfParam, updatedListOfSpec);
                            } else {
                                mapOfSpec.put(nameOfParam, Collections.singletonList(spec));
                            }
                        }));
        return mapOfSpec;
    }

    private Parameter buildSwaggerParameter(Map.Entry<String, List<Spec>> paramNameSpecEntry, Class<?> domainClass,
                                            Function<ResolvedType, ? extends ModelReference> factory) {
        Spec firstSpecFromList = paramNameSpecEntry.getValue().stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);
        ParamAttributes paramAttributes = getParamAttributes(firstSpecFromList, domainClass);

        return new ParameterBuilder()
                .parameterType(QUERY_PARAM)
                .name(paramNameSpecEntry.getKey())
                .modelRef(getModelReference(paramAttributes, factory))
                .description(buildParameterDescription(paramNameSpecEntry.getValue(), paramAttributes))
                .build();
    }

    private String buildParameterDescription(List<Spec> specList, ParamAttributes paramAttributes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Filter by '");
        stringBuilder.append(paramAttributes.getPath());
        stringBuilder.append("' field. Strategy for filtering - '");
        for (Spec specification : specList) {
            stringBuilder.append(specification.spec().getSimpleName());
            stringBuilder.append(" ");
        }
        stringBuilder.append("'.");
        if (ParamType.LIST.equals(paramAttributes.getParamType())) {
            stringBuilder.append(" Multiple params are supported.");
        }
        if (isDate(paramAttributes.getFieldType())) {
            String dateFormat = "yyyy-MM-dd";
            stringBuilder.append(" Date format: ");
            if (paramAttributes.getConfig() != null) {
                dateFormat = paramAttributes.getConfig();
            }
            stringBuilder.append(dateFormat);
        }
        return stringBuilder.toString();
    }

    private Optional<Class<?>> getFieldType(Class<?> specParam, Spec specification) {
        Optional<Class<?>> fieldClass = Optional.of(specParam);
        List<String> pathElements = Arrays.asList(specification.path().split("\\."));
        Iterator<String> pathIterator = pathElements.iterator();

        while (pathIterator.hasNext() && fieldClass.isPresent()) {
            Field field = FieldUtils.getField(fieldClass.get(), pathIterator.next(), true);
            fieldClass = field == null ? Optional.empty() : Optional.of(field.getType());
        }

        if (fieldClass.isPresent() && !ClassUtils.isPrimitiveOrWrapper(fieldClass.get()) && !isDate(fieldClass.get())) {
            return Optional.empty();
        }

        return fieldClass;
    }

    private List<String> getNamesOfParam(Spec spec) {
        return spec.params().length == 0 ? Collections.singletonList(spec.path()) : Arrays.asList(spec.params());
    }

    private ModelReference getModelReference(ParamAttributes paramAttributes,
                                             Function<ResolvedType, ? extends ModelReference> factory) {
        ModelReference model;
        if (ParamType.LIST.equals(paramAttributes.getParamType())) {
            model = factory.apply(resolver.resolve(List.class, String.class));
        } else if (ParamType.BOOLEAN.equals(paramAttributes.getParamType())) {
            model = factory.apply(resolver.resolve(Boolean.class));
        } else if (paramAttributes.getFieldType() == null) {
            model = factory.apply((resolver.resolve(String.class)));
        } else {
            model = factory.apply((resolver.resolve(paramAttributes.getFieldType())));
        }
        return model;
    }

    private Boolean isListParamType(Class<?> specRuleType) {
        return In.class.isAssignableFrom(specRuleType);
    }

    private Boolean isBooleanParamType(Class<?> specRuleType) {
        return Null.class.equals(specRuleType) || NotNull.class.equals(specRuleType);
    }

    private Boolean isDateBetweenParamType(Class<?> specRuleType) {
        return Between.class.equals(specRuleType);
    }

    private Boolean isDate(Class<?> fieldType) {
        return (fieldType == LocalDate.class || fieldType == java.util.Date.class || fieldType == java.sql.Date.class);
    }

    private ParamAttributes getParamAttributes(Spec spec, Class<?> domainClass) {
        ParamAttributes paramAttributes = new ParamAttributes();

        Optional<Class<?>> fieldType = getFieldType(domainClass, spec);
        if (fieldType.isPresent()) {
            paramAttributes.setFieldType(fieldType.get());
        }

        paramAttributes.setParamType(getParamType(spec.spec()));
        paramAttributes.setPath(spec.params().length == 0 || ParamType.DATE_BETWEEN.equals(paramAttributes.paramType)
                ? spec.path()
                : spec.params()[0]);
        if (spec.config().length != 0) {
            paramAttributes.setConfig(spec.config()[0]);
        }
        return paramAttributes;
    }

    private ParamType getParamType(Class<? extends Specification> specRuleType) {
        if (isListParamType(specRuleType)) {
            return ParamType.LIST;
        } else if (isBooleanParamType(specRuleType)) {
            return ParamType.BOOLEAN;
        } else if (isDateBetweenParamType(specRuleType)) {
            return ParamType.DATE_BETWEEN;
        } else {
            return ParamType.OTHER;
        }
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

    @Getter
    @Setter
    private class ParamAttributes {
        private Class<?> fieldType;
        private ParamType paramType;
        private String path;
        private String config;
    }

    private enum ParamType {
        LIST,
        BOOLEAN,
        DATE_BETWEEN,
        OTHER
    }
}