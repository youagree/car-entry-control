
package ru.unit_techno.car_entry_control.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import ru.unit_techno.car_entry_control.dto.RfidLabelDto;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.utils.BaseTestClass;
import ru.unit_techno.car_entry_control.utils.RestPageImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RfidControllerTest extends BaseTestClass {

    public static final String BASE_URL = "/ui/rfid";

    @Test
    @DisplayName("привязка машины к новой rfid метке")
    public void getFillRfid() {
        RfidLabel rfidLabel = rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(new Random().nextLong())
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

    @Test
    @DisplayName("Получение всех новых рфид меток по страницам по 20 шт.")
    public void getAllNewRfidLabels() {
        RfidLabel rfid1 = rfidLabelRepository.saveAndFlush(new RfidLabel()
                .setRfidLabelValue(123444L)
                .setCreationDate(Timestamp.valueOf(LocalDateTime.now()))
                .setState(StateEnum.NEW).setCar(null));

        RfidLabel rfid2 = rfidLabelRepository.saveAndFlush(new RfidLabel()
                .setRfidLabelValue(123777L)
                .setCreationDate(Timestamp.valueOf(LocalDateTime.now()))
                .setState(StateEnum.NEW).setCar(null));

        RfidLabel rfid3 = rfidLabelRepository.saveAndFlush(new RfidLabel()
                .setRfidLabelValue(666444L)
                .setCreationDate(Timestamp.valueOf(LocalDateTime.now()))
                .setState(StateEnum.NEW).setCar(null));

        String urlPage1 = BASE_URL + "/allRfidsByState?page=0&size=2&state=NEW";
        String urlPage2 = BASE_URL + "/allRfidsByState?page=1&size=2&state=NEW";
        RestPageImpl<RfidLabelDto> pageOfDto1 = testUtils.invokeGetApi(new ParameterizedTypeReference<RestPageImpl<RfidLabelDto>>() {
        }, urlPage1, HttpStatus.OK);
        RestPageImpl<RfidLabelDto> pageOfDto2 = testUtils.invokeGetApi(new ParameterizedTypeReference<RestPageImpl<RfidLabelDto>>() {
        }, urlPage2, HttpStatus.OK);

        List<RfidLabelDto> page1 = pageOfDto1.getContent();
        Assertions.assertEquals(rfid1.getRfidLabelValue(), page1.get(0).getRfidLabelValue());
        Assertions.assertEquals(rfid2.getRfidLabelValue(), page1.get(1).getRfidLabelValue());
        List<RfidLabelDto> page2 = pageOfDto2.getContent();
        Assertions.assertEquals(rfid3.getRfidLabelValue(), page2.get(0).getRfidLabelValue());
    }
}