
package ru.unit_techno.car_entry_control.utils;

import org.junit.jupiter.api.Test;
import ru.unit_techno.car_entry_control.util.Utils;

public class UtilsTest {

    @Test
    public void testBind() {
        Utils.bind(Exception::new, "test message");
    }
}