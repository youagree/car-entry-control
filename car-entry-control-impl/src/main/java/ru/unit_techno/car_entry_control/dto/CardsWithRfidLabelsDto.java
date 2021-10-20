package ru.unit_techno.car_entry_control.dto;

import lombok.Data;

@Data
public class CardsWithRfidLabelsDto {

    private Long rfidLabelValue;
    private String carModel;
    private String carColor;
    private String governmentNumber;
}
