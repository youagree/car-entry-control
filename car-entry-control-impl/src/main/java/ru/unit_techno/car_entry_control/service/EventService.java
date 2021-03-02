package ru.unit_techno.car_entry_control.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unit_techno.car_entry_control.aspect.RfidEvent;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.exception.custom.RfidAccessDeniedException;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final WSNotificationService notificationService;
    private final RfidLabelRepository rfidLabelRepository;

    public Long rfidLabelCheck (RfidEntry rfidLabel) throws Exception {
        Long longRfidLabel = rfidLabel.getRfid();
        Optional<RfidLabel> label = rfidLabelRepository.findByRfidLabelValue(longRfidLabel);
        rfidExceptionCheck(label);
        return label.get().getId();
    }

    @RfidEvent
    public void create(Long rfidLabel) {
        rfidLabelRepository.findByRfidLabelValue(rfidLabel).ifPresentOrElse(
                r -> rfidLabelRepository.save(new RfidLabel().setRfidLabelValue(rfidLabel)
                        .setState(StateEnum.NEW)),
                () -> {
                   throw new EntityExistsException();
                } );
    }

    private void rfidExceptionCheck(Optional<RfidLabel> rfidLabel) throws RfidAccessDeniedException {
        if (rfidLabel.isEmpty()) {
            throw new EntityNotFoundException("this rfid label is not in the database");
        }

        if (rfidLabel.get().getState().equals(StateEnum.NO_ACTIVE) ||
            rfidLabel.get().getState().equals(StateEnum.NEW)) {
            notificationService.sendNotActive(rfidLabel.get().getRfidLabelValue());
            throw new RfidAccessDeniedException("this rfid label is not active");
        }
    }
}
