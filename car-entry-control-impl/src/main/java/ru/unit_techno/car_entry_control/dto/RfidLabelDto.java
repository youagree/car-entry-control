package ru.unit_techno.car_entry_control.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RfidLabelDto {

    private Long rfidLabelValue;
    private LocalDateTime creationDate;
}
