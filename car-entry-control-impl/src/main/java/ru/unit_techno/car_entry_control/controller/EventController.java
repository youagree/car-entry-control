package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.unit_techno.car_entry_control.service.EventService;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/event")
    @ResponseStatus(HttpStatus.OK)
    public String eventHandler(String rfidLabel) throws Exception {
        return eventService.rfidLabelCheck(rfidLabel);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRfidLabel(Long rfidLabel) {
        eventService.create(rfidLabel);
    }
}
