package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Long> eventHandler(@RequestBody RfidEntry rfidEntry) throws Exception {
        eventService.rfidLabelCheck(rfidEntry);

        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(rfidEntry.getRfid().toString().length()));
//        headers.set(HttpHeaders.CONTENT_TYPE, "text/plain");
        return new ResponseEntity<>(rfidEntry.getDeviceId(), headers, HttpStatus.OK);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRfidLabel(@RequestParam Long rfidLabel) {
        eventService.create(rfidLabel);
    }
}
