package ru.unit_techno.car_entry_control.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RfidAccessDeniedException extends RuntimeException {

    public RfidAccessDeniedException(String file) {
        super(file);
    }

    public RfidAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RfidAccessDeniedException(Throwable cause) {
        super(cause);
    }

    public RfidAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RfidAccessDeniedException() {
    }
}
