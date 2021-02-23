
package ru.unit_techno.car_entry_control.dto;

import lombok.Data;

@Data
public class CarCreateDto {
    private String governmentNumber;
    private String carModel;
    private String carColour;
}