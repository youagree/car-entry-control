package ru.unit_techno.car_entry_control.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.unit.techno.ariss.log.action.lib.api.LogActionBuilder;
import ru.unit.techno.ariss.log.action.lib.entity.Description;
import ru.unit.techno.ariss.log.action.lib.model.ActionStatus;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.RfidLabel;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionCatchService {

    private final LogActionBuilder logActionBuilder;

    public void catchAction(RfidEntry rfidLabel,
                             RfidLabel rfid,
                             ActionStatus actionStatus,
                             Exception e) {
        logActionBuilder.buildActionObjectAndLogAction(
                rfidLabel.getDeviceId(),
                rfidLabel.getRfid(),
                rfid.getCar() != null ? rfid.getCar().getGovernmentNumber() : "НЕТ НОМЕРА",
                actionStatus,
                true,
                new Description()
                        .setStatusCode("500")
                        .setMessage(e.getMessage()));
    }

    public void catchActionWhenFeignException(RfidEntry rfidLabel,
                                               RfidLabel rfid,
                                               ActionStatus actionStatus,
                                               FeignException e) {
        logActionBuilder.buildActionObjectAndLogAction(
                rfidLabel.getDeviceId(),
                rfidLabel.getRfid(),
                rfid.getCar().getGovernmentNumber(),
                actionStatus,
                true,
                new Description().setStatusCode(String.valueOf(e.status()))
                        .setMessage(e.getMessage()).setErroredServiceName(e.request().url()));
    }
}
