package ru.unit_techno.car_entry_control.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.unit_techno.car_entry_control.exception.custom.CantCreateRfidLabelException;
import ru.unit_techno.car_entry_control.exception.custom.CarNotFoundException;
import ru.unit_techno.car_entry_control.exception.custom.RfidAccessDeniedException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({RfidAccessDeniedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleRfidAccessDeniedException() {
        log.error("Данная RFID метка не является активной, въезд запрещен.");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ExceptionHandler(CantCreateRfidLabelException.class)
    public void handleEntityExistFoundException(HttpServletRequest request, RuntimeException ex) {
        log.error("entity already exist", ex);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CarNotFoundException.class)
    public void handleEntityNotFoundException(HttpServletRequest request, RuntimeException ex) {
        log.error("entity not found", ex);
    }
}
