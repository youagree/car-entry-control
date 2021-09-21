
package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.unit_techno.car_entry_control.dto.RfidLabelDto;
import ru.unit_techno.car_entry_control.dto.request.EditRfidLabelRequest;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.service.RfidService;

import java.util.Date;

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
    @ResponseStatus(HttpStatus.OK)
    public void deleteNewRfidLabel(@PathVariable Long rfidId) {
        rfidService.deleteNewRfidLabel(rfidId);
    }

    @PostMapping("/deactivateUntil")
    @ResponseStatus(HttpStatus.OK)
    public void deactivateRfid(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date dateUntilDeactivated,
                               @RequestParam Long rfidLabelId) {
        rfidService.deactivateUntilSomeDate(dateUntilDeactivated, rfidLabelId);
    }

    @GetMapping("/allRfidsByState")
    public Page<RfidLabelDto> findAllRfidsWithNew(Pageable pageable, @RequestParam StateEnum state) {
        return rfidService.getAllNewRfidsWithPaging(pageable, state);
    }
}