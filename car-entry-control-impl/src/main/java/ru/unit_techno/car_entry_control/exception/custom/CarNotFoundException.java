
package ru.unit_techno.car_entry_control.exception.custom;


import javax.persistence.EntityNotFoundException;

public class CarNotFoundException extends EntityNotFoundException {
    public CarNotFoundException() {
    }

    public CarNotFoundException(String message) {
        super(message);
    }
}