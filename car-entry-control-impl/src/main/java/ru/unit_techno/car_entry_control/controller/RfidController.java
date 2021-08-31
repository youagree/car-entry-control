
package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.unit_techno.car_entry_control.dto.request.EditRfidLabelRequest;
import ru.unit_techno.car_entry_control.service.RfidService;

@RestController
@RequestMapping("/ui/rfid")
@RequiredArgsConstructor
public class RfidController {

    private final RfidService rfidService;

    @GetMapping("/getBlankRfid")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getBlankRfid(@RequestParam Long rfidId,
                             @RequestParam String governmentNumber) {
        rfidService.fillBlankRfidLabel(rfidId, governmentNumber);
    }

    @PostMapping("/blockRfid/{rfidId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockRfidLabel(@PathVariable Long rfidId) {
        rfidService.blockRfidLabel(rfidId);
    }

    @PostMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public void updateRfidLabel(@RequestBody EditRfidLabelRequest editRequest) {
        rfidService.editRfidLabel(editRequest);
    }

    @PutMapping("/resetRfid/{rfidId}")
    @ResponseStatus(HttpStatus.OK)
    public void resetRfidStatus(@PathVariable Long rfidId) {
        rfidService.resetRfidStatus(rfidId);
    }

    @DeleteMapping("/deleteRfid/{rfidId}")
    public void deleteNewRfidLabel(@PathVariable Long rfidId) {
        rfidService.deleteNewRfidLabel(rfidId);
    }
}