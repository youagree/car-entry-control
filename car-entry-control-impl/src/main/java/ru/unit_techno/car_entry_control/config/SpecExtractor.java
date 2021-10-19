
package ru.unit_techno.car_entry_control.config;

import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SpecExtractor {

    private static final Map<String, SpecExtractable> specExtractorMap;

    static {
        specExtractorMap = new HashMap<>();
        specExtractorMap.put(And.class.getSimpleName(), new AndSpecExtractor());
        specExtractorMap.put(Spec.class.getSimpleName(), new SimpleSpecExtractor());
    }

    public static List<Spec> extractSpecList(List<Annotation> annotations) {
        return annotations.stream()
                .filter(annotation -> specExtractorMap.containsKey(annotation.annotationType().getSimpleName()))
                .flatMap(annotation -> {
                    SpecExtractable specExtractable = specExtractorMap.get(annotation.annotationType().getSimpleName());
                    return specExtractable.extractSpec(annotation).stream();
                })
                .collect(Collectors.toList());
    }

    public interface SpecExtractable {

        List<Spec> extractSpec(Annotation annotation);
    }

    static class AndSpecExtractor implements SpecExtractable {
        @Override
        public List<Spec> extractSpec(Annotation annotation) {
            return Arrays.asList(((And) annotation).value());
        }
    }

    static class SimpleSpecExtractor implements SpecExtractable {
        @Override
        public List<Spec> extractSpec(Annotation annotation) {
            return Collections.singletonList((Spec) annotation);
        }
    }
}