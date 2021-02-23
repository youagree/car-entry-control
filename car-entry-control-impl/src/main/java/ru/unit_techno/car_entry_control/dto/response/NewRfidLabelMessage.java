package ru.unit_techno.car_entry_control.dto.response;

import lombok.Data;

@Data
public class NewRfidLabelMessage {

    private Long rfidLabelValue;
    private String message = "Активируйте новую метку";
}
