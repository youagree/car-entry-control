
package ru.unit_techno.car_entry_control.test_utils;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.unit_techno.car_entry_control.CarEntryControlApplication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureMockMvc
@SpringBootTest(classes = CarEntryControlApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public @interface IntegrationTest {
}