package ru.unit_techno.car_entry_control.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import ru.unit_techno.car_entry_control.aspect.RfidEvent;
import ru.unit_techno.car_entry_control.aspect.enums.RfidEventType;
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
@EnableAspectJAutoProxy
public class EventService {

    private final WSNotificationService notificationService;
    private final RfidLabelRepository rfidLabelRepository;

    @RfidEvent(value = RfidEventType.CHECK_RFID)
    public String rfidLabelCheck(RfidEntry rfidLabel) throws Exception {
        Long longRfidLabel = rfidLabel.getRfid();
        log.info("rfid id is: {}", longRfidLabel);
        Optional<RfidLabel> label = rfidLabelRepository.findByRfidLabelValue(longRfidLabel);
        rfidExceptionCheck(label);
        log.info("finish validate rfid, start open entry device");
        return label.get().getRfidLabelValue().toString();
    }

    @RfidEvent(value = RfidEventType.CREATE_RFID_LABEL)
    public void create(Long rfidLabel) {
        log.info("create new rfid label {}", rfidLabel);
        Optional<RfidLabel> foundedRfidLabel = rfidLabelRepository.findByRfidLabelValue(rfidLabel);
        if(foundedRfidLabel.isEmpty()) {
            rfidLabelRepository.save(new RfidLabel()
                                    .setRfidLabelValue(rfidLabel)
                                    .setState(StateEnum.NEW));
            return;
        }
        throw new EntityExistsException("rfid label is already exist");
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
