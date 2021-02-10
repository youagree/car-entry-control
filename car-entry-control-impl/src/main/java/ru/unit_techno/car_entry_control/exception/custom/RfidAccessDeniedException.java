package ru.unit_techno.car_entry_control.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RfidAccessDeniedException extends AccessDeniedException {

    public RfidAccessDeniedException(String file) {
        super(file);
    }

    public RfidAccessDeniedException(String file, String other, String reason) {
        super(file, other, reason);
    }
}
