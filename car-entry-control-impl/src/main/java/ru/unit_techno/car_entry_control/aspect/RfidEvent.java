package ru.unit_techno.car_entry_control.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RfidEvent {
    //TODO добавить value, в котором будет прописывать эвент тайп
}
