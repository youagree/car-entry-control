
package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.unit_techno.car_entry_control.service.WSNotificationService;

@RestController
@RequestMapping("/ui/testSocket")
@RequiredArgsConstructor
public class WebSocketTestController {

    private final WSNotificationService wsNotificationService;

    @PostMapping("/sendMessage")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCarWithBlankRfid(@RequestParam String zoneName, Long deviceId) {
        wsNotificationService.sendActiveButSomethingUnavailable(zoneName, deviceId);
    }
}