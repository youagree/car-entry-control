package ru.unit_techno.car_entry_control.aspect.enums;

import lombok.Getter;


public enum RfidEventType {
        CREATE_RFID_LABEL("Create rfid label"),
        CHECK_RFID("Check rfid"),
        FILL_BLANK_RFID("Fill blank rfid");

        @Getter
        private String value;

        RfidEventType (String value) {
            this.value = value;
        }

}
