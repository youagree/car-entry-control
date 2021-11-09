package ru.unit_techno.car_entry_control.exception.custom;

public class RfidScannerFatalErrorException extends RuntimeException {

    public RfidScannerFatalErrorException() {
    }

    public RfidScannerFatalErrorException(String message) {
        super(message);
    }
}
