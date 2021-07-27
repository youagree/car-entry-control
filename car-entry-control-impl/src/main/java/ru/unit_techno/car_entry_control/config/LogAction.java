
package ru.unit_techno.car_entry_control.config;

public interface LogAction<T> {
    void logSuccessAction(T actionObject);

    void logExceptionObject(T actionObject);
}