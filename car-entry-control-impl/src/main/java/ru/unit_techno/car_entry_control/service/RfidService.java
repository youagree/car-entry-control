
package ru.unit_techno.car_entry_control.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unit_techno.car_entry_control.dto.RfidLabelDto;
import ru.unit_techno.car_entry_control.dto.request.EditRfidLabelRequest;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.exception.custom.CannotLinkNewRfidLabelToCarException;
import ru.unit_techno.car_entry_control.mapper.RfidMapper;
import ru.unit_techno.car_entry_control.repository.CarRepository;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.stream.Collectors;

import static ru.unit_techno.car_entry_control.util.Utils.bind;

@Service
@RequiredArgsConstructor
public class RfidService {

    private final CarRepository carRepository;
    private final RfidLabelRepository rfidLabelRepository;
    private final RfidMapper rfidMapper;
    private final CarService carService;

    public void fillBlankRfidLabel(Long rfidId, String carId) {
        Car existCar = carRepository.findCarByGovernmentNumber(carId).orElseThrow(
                bind(CannotLinkNewRfidLabelToCarException::new, "car does not exist")
        );

        RfidLabel existRfid = rfidLabelRepository.findByRfidLabelValue(rfidId).orElseThrow(
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

    @Transactional
    public void resetRfidStatus(Long rfidId) {
        RfidLabel existRfid = rfidLabelRepository.findByRfidLabelValue(rfidId).orElseThrow(
                bind(CannotLinkNewRfidLabelToCarException::new, "rfid does not exist")
        );

        if (existRfid.getState() == StateEnum.ACTIVE || existRfid.getState() == StateEnum.NO_ACTIVE) {
            rfidLabelRepository.resetRfidLabelStatus(existRfid.getRfidLabelValue());
            carRepository.delete(existRfid.getCar());
        } else {
            throw new IllegalStateException("Cant update rfid label with this state " + existRfid.getState().getValue());
        }
    }

    @Transactional
    public void deleteNewRfidLabel(Long rfidId) {
        RfidLabel existRfid = rfidLabelRepository.findByRfidLabelValue(rfidId).orElseThrow(
                bind(CannotLinkNewRfidLabelToCarException::new, "rfid does not exist")
        );

        if (existRfid.getState() == StateEnum.NEW) {
            rfidLabelRepository.deleteByRfidLabelValue(rfidId);
        } else {
            throw new IllegalStateException("Only labels with the NEW status can be deleted." +
                    " State of this rfid label is " + existRfid.getState().getValue());
        }

    }

    @Transactional
    public void deactivateUntilSomeDate(Date dateUntilDeactivated, Long rfidLabelId) {
        RfidLabel existRfid = rfidLabelRepository.findByRfidLabelValue(rfidLabelId).orElseThrow(
                bind(CannotLinkNewRfidLabelToCarException::new, "rfid does not exist")
        );

        if (existRfid.getState() != StateEnum.NEW || existRfid.getState() != StateEnum.NO_ACTIVE) {
            rfidLabelRepository.deactivateRfidLabelUntilSomeDate(dateUntilDeactivated, rfidLabelId);
        } else {
            throw new IllegalStateException("Cant deactivate NEW or NO_ACTIVE rfid label");
        }
    }

    public Page<RfidLabelDto> getAllNewRfidsWithPaging(Pageable pageable, StateEnum state) {
        Page<RfidLabel> allByState = rfidLabelRepository.findAllByState(state, pageable);
        return new PageImpl<>(allByState.getContent().stream()
                .map(rfidMapper::toDto)
                .collect(Collectors.toList()), pageable, allByState.getTotalElements());
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