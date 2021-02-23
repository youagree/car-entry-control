package ru.unit_techno.car_entry_control.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RfidEntry {

    @JsonProperty("device_id")
    private Long deviceId;

    @JsonProperty("rfid")
    private Long rfid;
}
