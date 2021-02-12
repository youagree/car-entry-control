package ru.unit_techno.car_entry_control.dto.response;

import lombok.Data;

@Data
public class NewRfidLabelMessage {

    private Long id;
    private String message = "Активируй пжст новую метку";
}
