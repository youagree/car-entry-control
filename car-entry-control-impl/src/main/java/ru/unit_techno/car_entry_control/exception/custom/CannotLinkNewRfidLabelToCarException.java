
package ru.unit_techno.car_entry_control.exception.custom;

import javax.persistence.EntityExistsException;

public class CannotLinkNewRfidLabelToCarException extends EntityExistsException {
    public CannotLinkNewRfidLabelToCarException(String message) {
        super(message);
    }

    public CannotLinkNewRfidLabelToCarException() {
        super();
    }
}