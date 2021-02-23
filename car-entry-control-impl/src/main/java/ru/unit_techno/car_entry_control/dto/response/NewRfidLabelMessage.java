package ru.unit_techno.car_entry_control.dto.response;

import lombok.Data;

/**
 * !DTO для оповещения о созданиии новой метки
 */

@Data
public class NewRfidLabelMessage {

    private Long rfidLabelValue;
    private String message = "Активируйте новую метку";
}
