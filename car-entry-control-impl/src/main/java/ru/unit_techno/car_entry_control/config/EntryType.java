
package ru.unit_techno.car_entry_control.config;

import lombok.Getter;

public enum EntryType {
    IN("ВЪЕЗД"),
    OUT("ВЫЕЗД");

    @Getter
    private final String value;

    EntryType(String value) {
        this.value = value;
    }
}