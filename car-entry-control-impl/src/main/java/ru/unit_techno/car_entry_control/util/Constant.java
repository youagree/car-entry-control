
package ru.unit_techno.car_entry_control.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constant {
    public static final String REGEX = "(^[АВЕКМНОРСТУХ]\\d{3}(?<!000)[АВЕКМНОРСТУХ]{2}\\d{2,3})|(^[АВЕКМНОРСТУХ]{2}\\d{3}(?<!000)\\d{2,3})|(^[АВЕКМНОРСТУХ]{2}\\d{4}(?<!0000)\\d{2,3})|(^\\d{4}(?<!0000)[АВЕКМНОРСТУХ]{2}\\d{2,3})|(^[АВЕКМНОРСТУХ]{2}\\d{3}(?<!000)[АВЕКМНОРСТУХ]\\d{2,3})$";
}