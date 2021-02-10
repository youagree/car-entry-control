
package ru.unit_techno.car_entry_control.exception.custom;


import javax.persistence.EntityExistsException;

public class CantCreateRfidLabelException extends EntityExistsException {
    public CantCreateRfidLabelException(String message) {
        super(message);
    }

    public CantCreateRfidLabelException() {
        super();
    }
}