
package ru.unit_techno.car_entry_control.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.utils.BaseTestClass;

public class EventControllerTest extends BaseTestClass {

    public static final String BASE_URL = "/v1/";
    public static final String EVENT = BASE_URL + "event";

    @Test

    @DisplayName("Положительный кейс, когда метка существует и в статусе ACTIVE, -> шлагбаум открывается")
    public void eventHandlerGoodCase() {
        rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                        .setRfidLabelValue(124L)
                        .setState(StateEnum.ACTIVE)
        );
        testUtils.invokePostApi(Void.class, EVENT, HttpStatus.OK, new RfidEntry().setRfid(124L));
    }
}