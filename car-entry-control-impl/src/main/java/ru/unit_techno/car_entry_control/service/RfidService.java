
package ru.unit_techno.car_entry_control.service;

import static ru.unit_techno.car_entry_control.util.Utils.bind;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unit_techno.car_entry_control.dto.request.EditRfidLabelRequest;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.exception.custom.CannotLinkNewRfidLabelToCarException;
import ru.unit_techno.car_entry_control.repository.CarRepository;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RfidService {

    private final CarRepository carRepository;
    private final RfidLabelRepository rfidLabelRepository;
    private final CarService carService;

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

    @Transactional
    public void editRfidLabel(EditRfidLabelRequest editRequest) {
        RfidLabel existRfid = rfidLabelRepository.findByRfidLabelValue(editRequest.getRfidValue()).orElseThrow(
                bind(CannotLinkNewRfidLabelToCarException::new, "rfid does not exist")
        );

        if (existRfid.getState() == StateEnum.ACTIVE || existRfid.getState() == StateEnum.NO_ACTIVE) {
            updateRfid(existRfid, editRequest);
            rfidLabelRepository.save(existRfid);
        } else {
            throw new IllegalStateException("Cant update rfid label with status " + existRfid.getState().getValue());
        }
    }

    private void updateRfid(RfidLabel existRfid, EditRfidLabelRequest editRequest) {
        if (!editRequest.getGovernmentNumber().isEmpty()) {
            carService.validateGovernmentNumber(editRequest.getGovernmentNumber());
            existRfid.getCar().setGovernmentNumber(editRequest.getGovernmentNumber());
        }
        if (!editRequest.getCarColor().isEmpty()) {
            existRfid.getCar().setCarColour(editRequest.getCarColor());
        }
        if (!editRequest.getCarModel().isEmpty()) {
            existRfid.getCar().setCarModel(editRequest.getCarModel());
        }
        if (editRequest.getNewRfidValue() != null) {
            existRfid.setRfidLabelValue(editRequest.getNewRfidValue());
        }
    }
}