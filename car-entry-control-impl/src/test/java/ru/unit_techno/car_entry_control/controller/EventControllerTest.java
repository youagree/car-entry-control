
package ru.unit_techno.car_entry_control.controller;

import static org.mockito.ArgumentMatchers.any;

import feign.Request;
import lombok.SneakyThrows;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.unit.techno.ariss.barrier.api.BarrierFeignClient;
import ru.unit.techno.ariss.barrier.api.dto.BarrierRequestDto;
import ru.unit.techno.ariss.barrier.api.dto.BarrierResponseDto;
import ru.unit.techno.ariss.log.action.lib.entity.Description;
import ru.unit.techno.ariss.log.action.lib.entity.Event;
import ru.unit.techno.device.registration.api.DeviceResource;
import ru.unit.techno.device.registration.api.dto.DeviceResponseDto;
import ru.unit.techno.device.registration.api.enums.DeviceType;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.mapper.EntryDeviceToReqRespMapper;
import ru.unit_techno.car_entry_control.utils.AllFieldsNotNullRecursive;
import ru.unit_techno.car_entry_control.utils.BaseTestClass;

import java.util.List;
import java.util.Map;

public class EventControllerTest extends BaseTestClass {

    public static final String BASE_URL = "/v1/";
    public static final String EVENT = BASE_URL + "event";

    @MockBean
    private DeviceResource deviceResource;

    @MockBean
    private BarrierFeignClient barrierFeignClient;

    @Autowired
    private EntryDeviceToReqRespMapper reqRespMapper;

    @Test
    @DisplayName("Положительный кейс, когда метка существует и в статусе ACTIVE, -> шлагбаум открывается")
    public void eventHandlerGoodCase() {

        Mockito.when(deviceResource.getGroupDevices(5463L, DeviceType.RFID))
                .thenReturn(new DeviceResponseDto().setEntryAddress("unknown").setDeviceId(1239L).setType("ENTRY"));

        BarrierRequestDto barrierRequestDto = reqRespMapper.entryDeviceToRequest(new DeviceResponseDto()
                .setEntryAddress("unknown")
                .setDeviceId(1239L)
                .setType("ENTRY"));

        Mockito.when(barrierFeignClient.openBarrier(barrierRequestDto))
                .thenReturn(new BarrierResponseDto()
                        .setBarrierId(1239L)
                        .setBarrierResponseStatus(null));

        Car car = carRepository.saveAndFlush(
                new Car()
                        .setCarColor("RED")
                        .setGovernmentNumber("А777АА77")
                        .setCarModel("LADA")
        );

        rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(124L)
                        .setState(StateEnum.ACTIVE)
                        .setCar(car)
        );

        testUtils.invokePostApi(Void.class, EVENT, HttpStatus.OK, new RfidEntry()
                .setRfid(124L)
                .setDeviceId(5463L));
    }

    @Test
    @DisplayName("когда не найден такой рфид, но метаобджект найден, который берется из ямл")
    public void eventHandlerEntityNotFoundCase() {

        Car car = carRepository.saveAndFlush(
                new Car()
                        .setCarColor("RED")
                        .setGovernmentNumber("А777АА77")
                        .setCarModel("LADA")
        );

        rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(124L)
                        .setState(StateEnum.ACTIVE)
                        .setCar(car)
        );

        testUtils.invokePostApi(Void.class, EVENT, HttpStatus.NOT_FOUND, new RfidEntry()
                .setRfid(1L)
                .setDeviceId(7765L));

        List<Event> all = eventRepository.findAll();
        Event event = all.get(0);

        MatcherAssert.assertThat(event, AllFieldsNotNullRecursive.allFieldsNotNullRecursive(
                "Event.gosNumber",
                "Description.erroredServiceName"));
    }

    @Test
    @DisplayName("Кейс, при котором один из внешних сервисов недоступен или отработал с ошибкой и был получен FeignException")
    public void eventHandlerFeignCase() {

        Car car = carRepository.saveAndFlush(
                new Car()
                        .setCarColor("RED")
                        .setGovernmentNumber("А777АА77")
                        .setCarModel("LADA")
        );

        rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(124L)
                        .setState(StateEnum.ACTIVE)
                        .setCar(car)
        );

        Mockito.when(deviceResource.getGroupDevices(any(), any())).thenThrow(new FeignExceptionChild(500, "Barrier exception",
                Request.create(Request.HttpMethod.POST, "https://ariss.lifo.ru/barrier", Map.of(), null, null, null)));

        testUtils.invokePostApi(Void.class, EVENT, HttpStatus.INTERNAL_SERVER_ERROR, new RfidEntry()
                .setRfid(124L)
                .setDeviceId(7765L));

        Event recordedError = eventRepository.findAll().get(0);
        Assertions.assertEquals(recordedError.getCommonId(), 124L);
        Assertions.assertEquals(recordedError.getGosNumber(), "А777АА77");
        Assertions.assertEquals(recordedError.getEventType(), "ВЫЕЗД");
        Description description = recordedError.getDescription();
        Assertions.assertEquals(description.getMessage(), "Barrier exception");
        Assertions.assertEquals(description.getStatusCode(), "500");
        Assertions.assertEquals(description.getErroredServiceName(), "https://ariss.lifo.ru/barrier");
    }

    @SneakyThrows
    @Test
    @DisplayName("Кейс, при котором барьер недоступен или отработал с ошибкой и был получен FeignException")
    public void eventHandlerFeignBarrierCase() {

        Car car = carRepository.saveAndFlush(
                new Car()
                        .setCarColor("RED")
                        .setGovernmentNumber("А777АА77")
                        .setCarModel("LADA")
        );

        rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(124L)
                        .setState(StateEnum.ACTIVE)
                        .setCar(car)
        );



        Mockito.when(deviceResource.getGroupDevices(7765L, DeviceType.RFID))
                .thenReturn(new DeviceResponseDto().setEntryAddress("unknown").setDeviceId(1239L).setType("ENTRY"));

        Mockito.when(barrierFeignClient.openBarrier(any())).thenThrow(new FeignExceptionChild(500, "Barrier exception",
                Request.create(Request.HttpMethod.POST, "https://ariss.lifo.ru/barrier", Map.of(), null, null, null)));

        testUtils.invokePostApi(Void.class, EVENT, HttpStatus.OK, new RfidEntry()
                .setRfid(124L)
                .setDeviceId(7765L));

        Thread.sleep(1000);

        Event recordedError = eventRepository.findAll().get(0);
        Assertions.assertEquals(recordedError.getCommonId(), 124L);
        Assertions.assertEquals(recordedError.getGosNumber(), "А777АА77");
        Assertions.assertEquals(recordedError.getEventType(), "ВЫЕЗД");
        Description description = recordedError.getDescription();
        Assertions.assertEquals(description.getMessage(), "Barrier exception");
        Assertions.assertEquals(description.getStatusCode(), "500");
        Assertions.assertEquals(description.getErroredServiceName(), "https://ariss.lifo.ru/barrier");
    }

    @Test
    @DisplayName("когда не найден такой рфид и метобджект не найден, который берется из ямл")
    public void eventHandlerEntityNotFoundCaseAndMetaObjectNotFound() {

        Car car = carRepository.saveAndFlush(
                new Car()
                        .setCarColor("RED")
                        .setGovernmentNumber("А777АА77")
                        .setCarModel("LADA")
        );

        rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(124L)
                        .setState(StateEnum.ACTIVE)
                        .setCar(car)
        );

        testUtils.invokePostApi(Void.class, EVENT, HttpStatus.NOT_FOUND, new RfidEntry()
                .setRfid(1L)
                .setDeviceId(76656L));

        List<Event> all = eventRepository.findAll();
        Event event = all.get(0);
        MatcherAssert.assertThat(event, AllFieldsNotNullRecursive.allFieldsNotNullRecursive(
                "Event.gosNumber",
                "Description.erroredServiceName"));
    }

    @Test
    @DisplayName("когда не найден такой рфид и метобджект не найден, который берется из ямл")
    public void eventHandlerAccessDeniedExceptionAndMetaObjectNotFound() {

        Car car = carRepository.saveAndFlush(
                new Car()
                        .setCarColor("RED")
                        .setGovernmentNumber("А777АА77")
                        .setCarModel("LADA")
        );

        rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(124L)
                        .setState(StateEnum.NO_ACTIVE)
                        .setCar(car)
        );

        testUtils.invokePostApi(Void.class, EVENT, HttpStatus.UNAUTHORIZED, new RfidEntry()
                .setRfid(124L)
                // такой записи нету в ямл
                .setDeviceId(76651L));

        List<Event> all = eventRepository.findAll();
        Event event = all.get(0);
        MatcherAssert.assertThat(event, AllFieldsNotNullRecursive.allFieldsNotNullRecursive(
                "Event.gosNumber",
                "Description.erroredServiceName"));
    }

    @Test
    @DisplayName("когда не найден такой рфид и метобджект найден, который берется из ямл")
    public void eventHandlerAccessDeniedExceptionAndMetaObjectFound() {

        Car car = carRepository.saveAndFlush(
                new Car()
                        .setCarColor("RED")
                        .setGovernmentNumber("А777АА77")
                        .setCarModel("LADA")
        );

        rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(124L)
                        .setState(StateEnum.NO_ACTIVE)
                        .setCar(car)
        );

        testUtils.invokePostApi(Void.class, EVENT, HttpStatus.UNAUTHORIZED, new RfidEntry()
                .setRfid(124L)
                //есть описание в ямл
                .setDeviceId(7665L));

        List<Event> all = eventRepository.findAll();
        Event event = all.get(0);
        MatcherAssert.assertThat(event, AllFieldsNotNullRecursive.allFieldsNotNullRecursive(
                "Event.gosNumber",
                "Description.erroredServiceName"));
    }
}