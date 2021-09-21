package ru.unit_techno.car_entry_control.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class EditRfidLabelRequest {

    @JsonProperty("rfid_value")
    @NotBlank
    @NotNull
    private Long rfidValue;
    @JsonProperty("government_number")
    private String governmentNumber;
    @JsonProperty("car_model")
    private String carModel;
    @JsonProperty("car_color")
    private String carColor;
}
