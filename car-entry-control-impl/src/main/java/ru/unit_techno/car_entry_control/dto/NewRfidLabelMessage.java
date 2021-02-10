package ru.unit_techno.car_entry_control.dto;

import lombok.Data;

@Data
public class NewRfidLabelMessage {

    private Long id;
    String message = "Активируй пжст новую метку";
}
