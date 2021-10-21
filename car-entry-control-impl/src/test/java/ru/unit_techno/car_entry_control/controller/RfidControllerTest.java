
package ru.unit_techno.car_entry_control.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import ru.unit_techno.car_entry_control.dto.CarCreateDto;
import ru.unit_techno.car_entry_control.dto.CardsWithRfidLabelsDto;
import ru.unit_techno.car_entry_control.dto.RfidLabelDto;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.utils.BaseTestClass;
import ru.unit_techno.car_entry_control.utils.RestPageImpl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RfidControllerTest extends BaseTestClass {

    public static final String BASE_URL = "/ui/rfid";

    @Test
    @DisplayName("Создание новой машины и привязывание её к существующей метке")
    public void getFillRfid() {
        CarCreateDto carCreateDto = new CarCreateDto()
                .setCarColour("RED")
                .setGovernmentNumber("Т888ТТ 77")
                .setCarModel("LADA");

        RfidLabel rfidLabel = rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(1234511L)
                        .setState(StateEnum.NEW)
        );

        //привязываем неактивный рфид к машине и переводим в статус active
        String url = BASE_URL + "/createCarAndLinkRfid?rfidId=" + rfidLabel.getRfidLabelValue();
        testUtils.invokePostApi(Void.class, url, HttpStatus.CREATED, carCreateDto);

        Optional<RfidLabel> byId = rfidLabelRepository.findById(rfidLabel.getId());

        Assertions.assertEquals(byId.get().getState(), StateEnum.ACTIVE);
    }

    @Test
    @DisplayName("при привязке к рфид, рфид не был найден")
    public void getFillRfidBadRfidId() {
        CarCreateDto carCreateDto = new CarCreateDto()
                .setCarColour("RED")
                .setGovernmentNumber("Т888ТТ 77")
                .setCarModel("LADA");

        String url = BASE_URL + "/createCarAndLinkRfid?rfidId=" + 302;
        testUtils.invokePostApi(Void.class, url, HttpStatus.CONFLICT, carCreateDto);
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

    @Test
    @DisplayName("Создание машины и прикрепление этой машины к определенной метке")
    public void createCarWithBlankRfidTest() {
        Long rfidLabel = 99999L;
        String url = BASE_URL + "/createCarAndLinkRfid?rfidId=99999";

        rfidLabelRepository.save(new RfidLabel()
                .setRfidLabelValue(rfidLabel)
                .setState(StateEnum.NEW));

        CarCreateDto carCreateDto = new CarCreateDto()
                .setCarModel("BMW")
                .setCarColour("WHITE")
                .setGovernmentNumber("А888КК 77");

        testUtils.invokePostApi(Void.class, url, HttpStatus.CREATED, carCreateDto);

        Optional<RfidLabel> byRfidLabelValue = rfidLabelRepository.findByRfidLabelValue(rfidLabel);
        RfidLabel label = byRfidLabelValue.get();

        Assertions.assertEquals(label.getState(), StateEnum.ACTIVE);
        Assertions.assertEquals(label.getCar().getGovernmentNumber(), "А888КК 77");
    }

    @Test
    @DisplayName("Создание машины и прикрепление этой машины к метке, которой не существует")
    public void createCarAndLinkWrongRfid() {
        Long rfidLabel = 99999L;
        //Урл с несуществующей меткой
        String url = BASE_URL + "/createCarAndLinkRfid?rfidId=88888";

        rfidLabelRepository.save(new RfidLabel()
                .setRfidLabelValue(rfidLabel)
                .setState(StateEnum.NEW));

        CarCreateDto carCreateDto = new CarCreateDto()
                .setCarModel("BMW")
                .setCarColour("WHITE")
                .setGovernmentNumber("А888КК 77");

        testUtils.invokePostApi(Void.class, url, HttpStatus.CONFLICT, carCreateDto);
    }

    @Test
    @DisplayName("Создание машины с неправильный гос номером и прикрепление этой машины к метке")
    public void createCarWithWrongGovernmentNumberAndLinkRfid() {
        Long rfidLabel = 99999L;
        //Урл с несуществующей меткой
        String url = BASE_URL + "/createCarAndLinkRfid?rfidId=99999";

        rfidLabelRepository.save(new RfidLabel()
                .setRfidLabelValue(rfidLabel)
                .setState(StateEnum.NEW));

        CarCreateDto carCreateDto = new CarCreateDto()
                .setCarModel("BMW")
                .setCarColour("WHITE")
                //попытка привязать к машине с неверным номером
                .setGovernmentNumber("З123БС 99");

        testUtils.invokePostApi(Void.class, url, HttpStatus.BAD_REQUEST, carCreateDto);
    }

    @Test
    @DisplayName("Получение объектов рфид метка + тачка")
    public void getAllRfidsWithCarsTest() {
        RfidLabel rfid1 = rfidLabelRepository.saveAndFlush(new RfidLabel()
                .setRfidLabelValue(111444L)
                .setCreationDate(Timestamp.valueOf(LocalDateTime.now()))
                .setState(StateEnum.NEW)
                .setCar(null));

        RfidLabel rfid2 = rfidLabelRepository.saveAndFlush(new RfidLabel()
                .setRfidLabelValue(222444L)
                .setCreationDate(Timestamp.valueOf(LocalDateTime.now()))
                .setState(StateEnum.NEW)
                .setCar(null));

        RfidLabel rfid3 = rfidLabelRepository.saveAndFlush(new RfidLabel()
                .setRfidLabelValue(333444L)
                .setCreationDate(Timestamp.valueOf(LocalDateTime.now()))
                .setState(StateEnum.NEW)
                .setCar(null));

        Car save2 = carRepository.save(new Car()
                .setGovernmentNumber("А777АА 77")
                .setCarColour("RED")
                .setCarModel("ZHIGULL"));

        Car save1 = carRepository.save(new Car()
                .setGovernmentNumber("А222АА 77")
                .setCarColour("BLUE")
                .setCarModel("ZHIGULL"));

        Car save = carRepository.save(new Car()
                .setGovernmentNumber("А111АА 77")
                .setCarColour("BAKLAJAN")
                .setCarModel("ZHIGULL"));

        rfidLabelRepository.save(rfid1.setCar(save));
        rfidLabelRepository.save(rfid2.setCar(save2));
        rfidLabelRepository.save(rfid3.setCar(save1));

        String url = BASE_URL + "/allRfidsWithCars";

        RestPageImpl<CardsWithRfidLabelsDto> pageOfDto1 = testUtils.invokeGetApi(new ParameterizedTypeReference<RestPageImpl<CardsWithRfidLabelsDto>>() {
        }, url, HttpStatus.OK);

        CardsWithRfidLabelsDto assertDto = new CardsWithRfidLabelsDto()
                .setGovernmentNumber("А111АА 77")
                .setRfidLabelValue(111444L)
                .setCarColor("BAKLAJAN")
                .setCarModel("ZHIGULL");

        Assertions.assertEquals(pageOfDto1.getContent().size(), 3);
        Assertions.assertTrue(pageOfDto1.getContent().contains(assertDto));
    }

    @Test
    @DisplayName("постановка рфид на паузу")
    public void makePauseRfid() {
        Long rfidLabel = 123L;
        rfidLabelRepository.save(new RfidLabel()
                .setRfidLabelValue(rfidLabel)
                .setState(StateEnum.ACTIVE));

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedString = now.format(formatter);

        testUtils.invokePostApi(Void.class, BASE_URL + "/deactivateUntil?dateUntilDeactivated={1}&dateBefore={2}&rfidLabelId={3}",
                HttpStatus.OK,
                null,
                "2025-10-10", formattedString, 123);

        Optional<RfidLabel> byId = rfidLabelRepository.findByRfidLabelValue(rfidLabel);
        Assertions.assertNotNull(byId.get());
        Assertions.assertEquals(byId.get().getBeforeActiveUntil(), now);
        Assertions.assertEquals(byId.get().getNoActiveUntil(), LocalDate.of(2025, 10, 10));
    }
}
