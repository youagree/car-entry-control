package ru.unit_techno.car_entry_control.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RfidEntry {

    @JsonProperty("deviceId")
    private Long deviceId;

    @JsonProperty("rfidLabel")
    private Long rfid;
}
