package ru.unit_techno.car_entry_control.dto.response;

import lombok.Data;

@Data
public class BarrierUnavailable {

    private String barrierName;
    private Long deviceId;
    private String notificationMessage;
}
