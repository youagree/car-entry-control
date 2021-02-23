
package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.unit_techno.car_entry_control.dto.BlankRfidUpdateDto;
import ru.unit_techno.car_entry_control.service.RfidService;

@RestController
@RequestMapping("/ui/rfid")
@RequiredArgsConstructor
public class RfidController {

    private final RfidService rfidService;

    @GetMapping("/getBlankRfid")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getBlankRfid(BlankRfidUpdateDto blankRfidUpdateDto) {
        rfidService.fillBlankRfidLabel(blankRfidUpdateDto);
    }
}