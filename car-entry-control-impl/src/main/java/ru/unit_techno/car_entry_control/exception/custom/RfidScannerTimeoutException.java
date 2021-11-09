package ru.unit_techno.car_entry_control.exception.custom;

public class RfidScannerTimeoutException extends RuntimeException {

    public RfidScannerTimeoutException() {
    }

    public RfidScannerTimeoutException(String message) {
        super(message);
    }
}
