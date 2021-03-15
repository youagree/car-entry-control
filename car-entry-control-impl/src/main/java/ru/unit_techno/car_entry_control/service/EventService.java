package ru.unit_techno.car_entry_control.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final WSNotificationService notificationService;
    private final RfidLabelRepository rfidLabelRepository;

    public Long rfidLabelCheck (RfidEntry rfidLabel) throws Exception {
        log.info("start checking rfid label for access entry device: {}", rfidLabel);
        Long longRfidLabel = rfidLabel.getRfid();
        log.info("rfid id is: {}", longRfidLabel);
        Optional<RfidLabel> label = rfidLabelRepository.findByRfidLabelValue(longRfidLabel);
        rfidExceptionCheck(label);
        log.info("finish validate rfid, start open entry device");
        return label.get().getId();
    }

    @RfidEvent
    public void create(Long rfidLabel) {
        log.info("create new rfid label {}", rfidLabel);
        rfidLabelRepository.findByRfidLabelValue(rfidLabel).ifPresentOrElse(
                r -> rfidLabelRepository.save(new RfidLabel().setRfidLabelValue(rfidLabel)
                        .setState(StateEnum.NEW)),
                () -> {
                    log.info("this rfid already exist");
                   throw new EntityExistsException();
                } );
    }

    private void rfidExceptionCheck(Optional<RfidLabel> rfidLabel) throws RfidAccessDeniedException {
        if (rfidLabel.isEmpty()) {
            log.info("rfidLabel is empty, not exist");
            throw new EntityNotFoundException("this rfid label is not in the database");
        }

        if (rfidLabel.get().getState().equals(StateEnum.NO_ACTIVE) ||
            rfidLabel.get().getState().equals(StateEnum.NEW)) {
            log.info("rfidLabel is not active");
            notificationService.sendNotActive(rfidLabel.get().getRfidLabelValue());
            throw new RfidAccessDeniedException("this rfid label is not active");
        }
    }
}
