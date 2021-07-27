
package ru.unit_techno.car_entry_control.config;

import lombok.Getter;

public enum ActionStatus {
    UNKNOWN("неизвестный"),
    ACTIVE("активны"),
    STOP("остановлен/просрочен");

    @Getter
    private final String value;

    ActionStatus(String value) {
        this.value = value;
    }
}