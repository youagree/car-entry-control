
package ru.unit_techno.car_entry_control.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import ru.unit.techno.ariss.barrier.api.BarrierFeignClient;
import ru.unit.techno.ariss.barrier.api.dto.BarrierRequestDto;
import ru.unit.techno.ariss.barrier.api.dto.BarrierResponseDto;
import ru.unit.techno.device.registration.api.DeviceResource;
import ru.unit.techno.device.registration.api.dto.DeviceResponseDto;
import ru.unit.techno.device.registration.api.enums.DeviceType;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.mapper.EntryDeviceToReqRespMapper;
import ru.unit_techno.car_entry_control.utils.BaseTestClass;

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
                        .setCarColour("RED")
                        .setGovernmentNumber("А777АА 77")
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
}