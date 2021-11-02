package ru.unit_techno.car_entry_control.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;

import java.time.LocalDate;

@Data
public class CardsWithRfidLabelsDto {

    private Long rfidLabelValue;
    private String carModel;
    private String carColor;
    private String governmentNumber;
    private StateEnum state;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate noActiveUntil;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate beforeActiveUntil;
}
