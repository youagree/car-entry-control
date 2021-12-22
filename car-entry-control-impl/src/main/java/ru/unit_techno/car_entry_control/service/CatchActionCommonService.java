package ru.unit_techno.car_entry_control.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.unit.techno.ariss.log.action.lib.config.DeviceEventConfig;
import ru.unit.techno.ariss.log.action.lib.model.ActionStatus;
import ru.unit.techno.ariss.log.action.lib.model.EntryType;
import ru.unit.techno.ariss.log.action.lib.model.MetaObject;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.exception.custom.RfidAccessDeniedException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

import static ru.unit_techno.car_entry_control.util.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatchActionCommonService {

    private final WSNotificationService notificationService;
    private final ActionCatchService actionCatchService;
    private final DeviceEventConfig eventConfig;


    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void rfidNotFoundCatchAndSaveAction(RfidEntry rfidLabel, Long longRfidLabel, EntityNotFoundException e) {
        log.error("unknown rfid label", e);
        MetaObject metaObject = Optional.ofNullable(eventConfig.getType().get(rfidLabel.getDeviceId()))
                .orElse(new MetaObject().setEntryType(EntryType.UNKNOWN).setInfo("неизвестная ошибка"));
        log.info("send notification to websocket with props, rfid: {}, ex is {}", rfidLabel, e);
        notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo(), rfidLabel.getDeviceId(), RFID_NOT_FOUND_MESSAGE);
        actionCatchService.catchAction(rfidLabel, new RfidLabel()
                        .setCar(new Car()
                                .setGovernmentNumber(null))
                        .setRfidLabelValue(longRfidLabel),
                ActionStatus.UNKNOWN, e);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void rfidAccessDeniedCatchAndSave(RfidEntry rfidLabel, RfidLabel longRfidLabel, ActionStatus actionStatus, RfidAccessDeniedException e) {
        log.info("send notification to websocket with props, rfid: {}, ex is {}", rfidLabel, e);
        notificationService.sendNotActive(longRfidLabel.getRfidLabelValue(), RFID_NOT_ACTIVE_MESSAGE);
        log.error("rfid label not active", e);
        actionCatchService.catchAction(rfidLabel, longRfidLabel, actionStatus, e);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public void feignExceptionCheckAndSave(RfidEntry rfidLabel, Optional<RfidLabel> label, FeignException e) {
        MetaObject metaObject = Optional.ofNullable(eventConfig.getType().get(rfidLabel.getDeviceId())).orElse(
                new MetaObject().setEntryType(EntryType.UNKNOWN).setInfo("неизвестная ошибка")
        );
        log.info("send notification to websocket with props, rfid: {}, ex is {}", rfidLabel, e);
        notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo(), rfidLabel.getDeviceId(), RFID_UNKNOWN_EXCEPTION_MESSAGE);
        log.error("Service not available", e);
        actionCatchService.catchActionWhenFeignException(rfidLabel, label.get(), ActionStatus.ACTIVE, e);
    }
}