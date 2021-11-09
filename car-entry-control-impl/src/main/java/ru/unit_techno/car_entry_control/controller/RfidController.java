
package ru.unit_techno.car_entry_control.controller;

import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.JoinFetch;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.unit_techno.car_entry_control.dto.CarCreateDto;
import ru.unit_techno.car_entry_control.dto.CardsWithRfidLabelsDto;
import ru.unit_techno.car_entry_control.dto.RfidLabelDto;
import ru.unit_techno.car_entry_control.dto.request.EditRfidLabelRequest;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
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
    public void createCarWithBlankRfid(@RequestBody CarCreateDto carCreateDto) {
        rfidService.createCardAndLinkRfid(carCreateDto);
    }

    @PostMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public void updateRfidLabel(@RequestBody EditRfidLabelRequest editRequest) {
        rfidService.editRfidLabel(editRequest);
    }

    @PutMapping("/resume/{rfidId}")
    @ResponseStatus(HttpStatus.OK)
    public void resumeRfidLabel(@PathVariable Long rfidId) {
        rfidService.resumeRfidLabelState(rfidId);
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

    //todo покрыть тестом
    //todo не работает сортировка по госномер
    @GetMapping("/allRfidsWithCars")
    public Page<CardsWithRfidLabelsDto> findAllRfidsWithCard(
            @JoinFetch(paths = "car")
            @And({@Spec(path = "rfidLabelValue", params = "rfidLabelValue", spec = Equal.class),
                    @Spec(path = "car.governmentNumber", params = "governmentNumber", spec = Equal.class)
            }) Specification<RfidLabel> specificationPageable, Pageable pageable) {
        return rfidService.getAllRfidsWithCars(pageable, specificationPageable);
    }
}