
package ru.unit_techno.car_entry_control.service;

import static ru.unit_techno.car_entry_control.util.Utils.bind;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unit_techno.car_entry_control.dto.BlankRfidUpdateDto;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.repository.CarRepository;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class RfidService {

    private final CarRepository carRepository;
    private final RfidLabelRepository rfidLabelRepository;

    public void fillBlankRfidLabel(BlankRfidUpdateDto blankRfidUpdateDto) {
        Car existCar = carRepository.findById(blankRfidUpdateDto.getRfidId()).orElseThrow(
                bind(EntityNotFoundException::new, "car does not exist")
        );

        RfidLabel existRfid = rfidLabelRepository.findById(blankRfidUpdateDto.getRfidId()).orElseThrow(
                bind(EntityNotFoundException::new, "rfid does not exist")
        );
        rfidLabelRepository.save(existRfid.setState(StateEnum.ACTIVE)
                                          .setCar(existCar));

    }
}