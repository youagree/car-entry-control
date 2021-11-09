package ru.unit_techno.car_entry_control.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;

import java.time.LocalDate;

@Data
public class CardsWithRfidLabelsDto {

    private Long rfidLabelValue;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String carModel;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String carColor;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String governmentNumber;
    private StateEnum state;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate pauseRfidTo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate pauseRfidFrom;
}
