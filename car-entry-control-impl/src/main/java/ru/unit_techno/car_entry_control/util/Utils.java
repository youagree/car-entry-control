
package ru.unit_techno.car_entry_control.util;

import lombok.experimental.UtilityClass;

import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class Utils {

    public static <T, R> Supplier<R> bind(Function<T,R> fn, T val) {
        return () -> fn.apply(val);
    }
}