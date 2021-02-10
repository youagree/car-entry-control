package ru.unit_techno.car_entry_control.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.exception.RfidAccessDeniedException;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class EventService {

    private RfidLabelRepository rfidLabelRepository;

    @Autowired
    public EventService(RfidLabelRepository rfidLabelRepository) {
        this.rfidLabelRepository = rfidLabelRepository;
    }

    public String rfidLabelCheck (String rfidLabel) throws Exception {
        Long longRfidLabel = Long.parseLong(rfidLabel);
        Optional<RfidLabel> label = rfidLabelRepository.findRfidLabelByRfidLabelValue(longRfidLabel);

        if (label.isEmpty()) {
            throw new EntityNotFoundException("this rfid label is not in the database");
        }

        if (!label.get().isACTIVE()) {
            throw new RfidAccessDeniedException("this rfid label is not active");
        }

        return label.get().getRfidLabelValue().toString();
    }
}
