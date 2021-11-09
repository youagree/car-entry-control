package ru.unit_techno.car_entry_control.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.unit_techno.car_entry_control.dto.ExceptionHandleDto;
import ru.unit_techno.car_entry_control.exception.custom.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
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

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CannotLinkNewRfidLabelToCarException.class)
    public void handleCannotLinkNewRfidLabelToCarException(HttpServletRequest request, RuntimeException ex) {
        log.error("cannot link new rfid label", ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ExceptionHandleDto handleIllegalArgumentException(HttpServletRequest request, RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return new ExceptionHandleDto().setMessage(ex.getMessage()).setStatusCode(400);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ExceptionHandleDto handleIllegalEntityNotFoundException(HttpServletRequest request, EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ExceptionHandleDto().setStatusCode(404).setMessage(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EntityExistsException.class)
    public ExceptionHandleDto handleEntityExistException(HttpServletRequest request, EntityExistsException ex) {
        log.error(ex.getMessage(), ex);
        return new ExceptionHandleDto().setStatusCode(409).setMessage(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    @ExceptionHandler(RfidScannerTimeoutException.class)
    public ExceptionHandleDto handleScannerTimeoutException(HttpServletRequest request, RfidScannerTimeoutException ex) {
        log.error(ex.getMessage(), ex);
        return new ExceptionHandleDto().setMessage(ex.getMessage()).setStatusCode(408);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RfidScannerFatalErrorException.class)
    public ExceptionHandleDto handleRfidScannerFatalError(HttpServletRequest request, RfidScannerFatalErrorException ex) {
        log.error(ex.getMessage(), ex);
        return new ExceptionHandleDto().setMessage(ex.getMessage()).setStatusCode(500);
    }
}
