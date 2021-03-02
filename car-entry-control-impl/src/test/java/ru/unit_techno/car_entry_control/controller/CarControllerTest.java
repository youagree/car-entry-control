
package ru.unit_techno.car_entry_control.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.unit_techno.car_entry_control.dto.CarCreateDto;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.utils.BaseTestClass;

import java.util.List;

public class CarControllerTest extends BaseTestClass {

    public static final String BASE_URL = "/ui/car";

    @Test
    public void carControllerTest() {
        String testUrl = BASE_URL + "/create";
        testUtils.invokePostApi(Void.class, testUrl, HttpStatus.CREATED, new CarCreateDto()
                .setCarColour("RED")
                .setCarModel("kamaz")
                .setGovernmentNumber("test_number"));

        List<Car> byId = carRepository.findAll();

        assertEquals(byId.get(0).getCarColour(), "RED");
        assertEquals(byId.get(0).getCarModel(), "kamaz");
        assertEquals(byId.get(0).getGovernmentNumber(), "test_number");
    }
}