package ru.unit_techno.car_entry_control.controller;

import feign.FeignException;
import feign.Request;

public class FeignExceptionChild extends FeignException {

    protected FeignExceptionChild(int status, String message, Request request) {
        super(status, message, request);
    }
}
