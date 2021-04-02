package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.service.EventService;
import ru.unit_techno.car_entry_control.service.GateService;

@RestController
@RequestMapping("v1")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final GateService gateService;

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
        headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(rfidEntry.getRfid().toString().length()));
        return new ResponseEntity<>(rfidEntry.getDeviceId(), headers, HttpStatus.OK);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRfidLabel(@RequestParam Long rfidLabel) {
        eventService.create(rfidLabel);
    }

    @PostMapping("/forceOpen")
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public void forceOpenGate() {
        //todo Добавить метод на принудительное открытие шлагбаума прям через прошивку
        gateService.forceOpenGate();
    }

}
