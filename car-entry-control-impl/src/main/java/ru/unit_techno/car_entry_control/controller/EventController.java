package ru.unit_techno.car_entry_control.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.unit_techno.car_entry_control.entity.Event;
import ru.unit_techno.car_entry_control.service.EventService;

@RestController
@RequestMapping("v1")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/event")
    @ResponseStatus(HttpStatus.OK)
    public String eventHandler(String rfidLabel) throws Exception {
        return eventService.rfidLabelCheck(rfidLabel);
//      return "S1\r\nIN\r\n";
    }

}
