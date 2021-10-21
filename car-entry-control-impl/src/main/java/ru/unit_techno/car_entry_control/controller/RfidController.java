
package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.unit_techno.car_entry_control.dto.CarCreateDto;
import ru.unit_techno.car_entry_control.dto.CardsWithRfidLabelsDto;
import ru.unit_techno.car_entry_control.dto.RfidLabelDto;
import ru.unit_techno.car_entry_control.dto.request.EditRfidLabelRequest;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.service.RfidService;

import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

@RestController
@RequestMapping("/ui/rfid")
@RequiredArgsConstructor
public class RfidController {

    private final RfidService rfidService;

    @PostMapping("/createCarAndLinkRfid")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCarWithBlankRfid(@RequestParam Long rfidId,
                                       @RequestBody CarCreateDto carCreateDto) {
        rfidService.createCardAndLinkRfid(rfidId, carCreateDto);
    }

    @PostMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public void updateRfidLabel(@RequestBody EditRfidLabelRequest editRequest) {
        rfidService.editRfidLabel(editRequest);
    }

    @PostMapping("/resume/{rfidId}")
    @ResponseStatus(HttpStatus.OK)
    public void resumeRfidLabel() {

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
    public void deactivateRfid(@Valid @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @FutureOrPresent LocalDate dateUntilDeactivated,
                               @Valid @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @FutureOrPresent LocalDate dateBefore,
                               @RequestParam Long rfidLabelId) {
        rfidService.deactivateUntilSomeDate(dateBefore, dateUntilDeactivated, rfidLabelId);
    }

    @GetMapping("/allRfidsByState")
    public Page<RfidLabelDto> findAllRfidsWithNew(Pageable pageable, @RequestParam StateEnum state) {
        return rfidService.getAllNewRfidsWithPaging(pageable, state);
    }

    @GetMapping("/allRfidsWithCars")
    public Page<CardsWithRfidLabelsDto> findAllRfidsWithCard(Pageable pageable) {
        return rfidService.getAllRfidsWithCars(pageable);
    }
}