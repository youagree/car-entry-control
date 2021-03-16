
package ru.unit_techno.car_entry_control.service;

import static ru.unit_techno.car_entry_control.util.Utils.bind;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.exception.custom.CannotLinkNewRfidLabelToCarException;
import ru.unit_techno.car_entry_control.repository.CarRepository;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class RfidService {

    private final CarRepository carRepository;
    private final RfidLabelRepository rfidLabelRepository;

    public void fillBlankRfidLabel(Long rfidId, Long carId) {
        Car existCar = carRepository.findById(rfidId).orElseThrow(
                bind(CannotLinkNewRfidLabelToCarException::new, "car does not exist")
        );

        RfidLabel existRfid = rfidLabelRepository.findById(carId).orElseThrow(
                bind(CannotLinkNewRfidLabelToCarException::new, "rfid does not exist")
        );
        rfidLabelRepository.save(existRfid.setState(StateEnum.ACTIVE)
                .setCar(existCar));

    }

    public void blockRfidLabel(Long rfidId) {
        RfidLabel rfidLabel = rfidLabelRepository.findByRfidLabelValue(rfidId).orElseThrow(bind(EntityNotFoundException::new, "This rfidLabel not found"));
        rfidLabel.setState(StateEnum.BLOCK);
        rfidLabelRepository.save(rfidLabel);
    }
}