package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.service.EventService;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * takes rfidEntry, returns device_id to open it
     *
     * @param rfidEntry id and rfidLabel
     * @return rfidEntry.device_id
     * @throws Exception
     */
    @PostMapping("/event")
    @ResponseStatus(HttpStatus.OK)
    public void eventHandler(@RequestBody RfidEntry rfidEntry) {
        eventService.rfidLabelCheck(rfidEntry);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRfidLabel() {
        eventService.create();
    }
}
