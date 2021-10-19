
package ru.unit_techno.car_entry_control.config;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(Predicates.or(basePackage("ru.unit_techno.car_entry_control"), basePackage("ru.unit.techno.ariss.log.action.lib")))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public PageableForSwagger swaggerOperationParameterReader(TypeNameExtractor nameExtractor,
                                                              TypeResolver resolver) {
        return new PageableForSwagger(nameExtractor, resolver);
    }
}