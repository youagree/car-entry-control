package ru.unit_techno.car_entry_control.entity;

import lombok.Data;

@Data
public class Description {
    private String statusCode;
    private String message;
    private String erroredServiceName;
}
