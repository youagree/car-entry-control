
package ru.unit_techno.car_entry_control.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.utils.BaseTestClass;

import java.util.Optional;

public class RfidControllerTest extends BaseTestClass {

    public static final String BASE_URL = "/ui/rfid";

    @Test
    @DisplayName("привязка машины к новой rfid метке")
    public void getFillRfid() {
        RfidLabel rfidLabel = rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(124L)
                        .setState(StateEnum.NEW)
        );

        Car car = carRepository.saveAndFlush(
                new Car()
                .setCarColour("RED")
        );

        //привязываем неактивный рфид к машине и переводим в статус active
        String url = BASE_URL + "/getBlankRfid?rfidId=" + rfidLabel.getId() + "&" +
                "carId=" + car.getId();
        testUtils.invokeGetApi(Void.class, url, HttpStatus.NO_CONTENT, null);

        Optional<RfidLabel> byId = rfidLabelRepository.findById(rfidLabel.getId());

        Assertions.assertEquals(byId.get().getState(), StateEnum.ACTIVE);
    }

    @Test
    @DisplayName("при привязке к рфид, машина не была найдена")
    public void getFillRfidBadCarId() {
        RfidLabel rfidLabel = rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(125L)
                        .setState(StateEnum.NEW)
        );
        //привязываем неактивный рфид к машине и переводим в статус active
        String url = BASE_URL + "/getBlankRfid?rfidId=" + rfidLabel.getId() + "&" +
                "carId=" + 30;
        testUtils.invokeGetApi(Void.class, url, HttpStatus.CONFLICT, null);

        Optional<RfidLabel> byId = rfidLabelRepository.findById(rfidLabel.getId());

        Assertions.assertEquals(byId.get().getState(), StateEnum.NEW);
    }

    @Test
    @DisplayName("при привязке к рфид, рфид не был найден")
    public void getFillRfidBadRfidId() {
        Car car = carRepository.saveAndFlush(new Car()
                .setCarColour("RED")
        );

        //привязываем неактивный рфид к машине и переводим в статус active
        String url = BASE_URL + "/getBlankRfid?rfidId=" + 302 + "&" +
                "carId=" + car.getId();
        testUtils.invokeGetApi(Void.class, url, HttpStatus.CONFLICT, null);
    }
}