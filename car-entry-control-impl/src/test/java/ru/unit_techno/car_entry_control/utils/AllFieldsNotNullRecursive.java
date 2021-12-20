package ru.unit_techno.car_entry_control.utils;

import static java.util.Arrays.asList;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.beans.PropertyUtil;
import org.hibernate.validator.internal.util.ReflectionHelper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * uber_dev
 *
 * @param <T>
 */
@Slf4j
public class AllFieldsNotNullRecursive<T> extends TypeSafeMatcher<T> {
    private final List<String> excludeFields = new ArrayList<>();
    private T object;

    private final List<String> nullFields = new ArrayList<>();

    public AllFieldsNotNullRecursive(String... excludeFields) {
        this.excludeFields.addAll(asList(excludeFields));
    }

    public static <T> Matcher<T> allFieldsNotNullRecursive() {
        return new AllFieldsNotNullRecursive<>();
    }

    public static <T> Matcher<T> allFieldsNotNullRecursive(String... excludeFields) {
        return new AllFieldsNotNullRecursive<>(excludeFields);
    }

    @Override
    public boolean matchesSafely(T object) {
        this.object = object;
        check(object);
        return nullFields.isEmpty();
    }

    private void check(Object object) {
        Class<?> clazz = object.getClass();
        while (clazz != null && clazz != Object.class && !clazz.isPrimitive()) {
            for (Field field : clazz.getDeclaredFields()) {
                checkField(object, field);
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Check field's value for nullability. Check only fields accessible through getters, ignore other.
     *
     * @param object object
     * @param field  field
     */
    @SneakyThrows
    private void checkField(Object object, Field field) {

        Method getter = getter(object, field);
        if (getter == null) {
            return;
        }
        Object fieldValue = getter.invoke(object);
        if (isFieldValueNull(object, field, fieldValue)) return;
        boolean iterable = ReflectionHelper.isIterable(field.getType());
        if (iterable) {
            for (Object item : (Iterable) fieldValue) {
                if (isFieldValueNull(object, field, item)) return;
                check(item);
            }
        } else {
            check(fieldValue);
        }
    }

    private boolean isFieldValueNull(Object object, Field field, Object fieldValue) {
        String fieldName = object.getClass().getSimpleName() + "." + field.getName();
        if (fieldValue == null) {
            if (!excludeFields.contains(fieldName)) {
                nullFields.add(fieldName);
            }
            return true;
        }
        return false;
    }

    private Method getter(Object object, Field field) {
        if (field.getType().isPrimitive()) {
            return null;
        }
        PropertyDescriptor propertyDescriptor = PropertyUtil.getPropertyDescriptor(field.getName(), object);
        if (propertyDescriptor == null) {
            return null;
        }
        return propertyDescriptor.getReadMethod();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("All fields must not be null!");
    }

    @Override
    public void describeMismatchSafely(T item, Description description) {
        description.appendText("Found fields with null values: ")
                .appendValue(nullFields)
                .appendText("\n\t object: ")
                .appendValue(object);
    }
}