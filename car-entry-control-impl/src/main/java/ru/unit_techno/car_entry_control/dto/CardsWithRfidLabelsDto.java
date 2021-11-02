package ru.unit_techno.car_entry_control.dto;

import lombok.Data;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;

@Data
public class CardsWithRfidLabelsDto {

    private Long rfidLabelValue;
    private String carModel;
    private String carColor;
    private String governmentNumber;
    private StateEnum state;
}
