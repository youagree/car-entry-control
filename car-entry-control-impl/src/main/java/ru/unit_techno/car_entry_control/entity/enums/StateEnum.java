
package ru.unit_techno.car_entry_control.entity.enums;

import lombok.Getter;

public enum StateEnum {
    ACTIVE("active"),
    NO_ACTIVE("no_active"),
    NEW("new"),
    BLOCK("blocked");

    @Getter
    private String value;

    StateEnum(String value) {
        this.value = value;
    }
}