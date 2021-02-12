package ru.unit_techno.car_entry_control.service;

import static ru.unit_techno.car_entry_control.util.Utils.bind;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.dto.response.NewRfidLabelMessage;
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

    private final RfidLabelRepository rfidLabelRepository;
    private final SimpMessagingTemplate brokerMessagingTemplate;

    public String rfidLabelCheck (RfidEntry rfidLabel) throws Exception {
        Long longRfidLabel = Long.parseLong(rfidLabel.getRfid());
        Optional<RfidLabel> label = rfidLabelRepository.findRfidLabelByRfidLabelValue(longRfidLabel);
        rfidExceptionCheck(label);
        return label.get().getRfidLabelValue().toString();
    }

    public void create(Long rfidLabel) {
        rfidLabelRepository.findRfidLabelByRfidLabelValue(rfidLabel).orElseThrow(
                bind(EntityExistsException::new, "this rfidLabel already exist"));
        rfidLabelRepository.save(new RfidLabel().setRfidLabelValue(rfidLabel)
                                                .setState(StateEnum.NEW.getValue()));
    }

    private void rfidExceptionCheck(Optional<RfidLabel> rfidLabel) throws RfidAccessDeniedException {
        if (rfidLabel.isEmpty()) {
            throw new EntityNotFoundException("this rfid label is not in the database");
        }

        if (rfidLabel.get().getState().equals(StateEnum.NO_ACTIVE.getValue()) ||
            rfidLabel.get().getState().equals(StateEnum.NEW.getValue())) {
            brokerMessagingTemplate.convertAndSend(
                    "/topic/newrfidlabel",
                    new NewRfidLabelMessage()
                            .setId(rfidLabel.get().getId())
            );
            throw new RfidAccessDeniedException("this rfid label is not active");
        }
    }
}
