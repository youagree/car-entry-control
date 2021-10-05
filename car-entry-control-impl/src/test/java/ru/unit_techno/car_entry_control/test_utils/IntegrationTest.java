
package ru.unit_techno.car_entry_control.test_utils;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.unit_techno.car_entry_control.CarEntryControlApplication;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = CarEntryControlApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public @interface IntegrationTest {
}