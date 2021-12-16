package ru.unit_techno.car_entry_control.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.unit.techno.ariss.barrier.api.BarrierFeignClient;
import ru.unit.techno.ariss.barrier.api.dto.BarrierRequestDto;
import ru.unit.techno.ariss.log.action.lib.api.LogActionBuilder;
import ru.unit.techno.ariss.log.action.lib.config.DeviceEventConfig;
import ru.unit.techno.ariss.log.action.lib.model.ActionStatus;
import ru.unit.techno.ariss.log.action.lib.model.MetaObject;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.RfidLabel;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarrierFeignService {

    private final BarrierFeignClient barrierFeignClient;
    private final WSNotificationService notificationService;
    private final LogActionBuilder logActionBuilder;
    private final ActionCatchService actionCatchService;
    private final DeviceEventConfig eventConfig;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void openBarrier(BarrierRequestDto barrierRequestDto, RfidLabel rfidLabel, RfidEntry entry) {
        try {
            barrierFeignClient.openBarrier(barrierRequestDto);
            logActionBuilder.buildActionObjectAndLogAction(entry.getDeviceId(),
                    entry.getRfid(),
                    rfidLabel.getCar().getGovernmentNumber(),
                    ActionStatus.ACTIVE);
        } catch (FeignException e) {
            MetaObject metaObject = eventConfig.getType().get(barrierRequestDto.getBarrierId());
            notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo(), barrierRequestDto.getBarrierId());
            log.error("Service not available", e);
            actionCatchService.catchActionWhenFeignException(entry, rfidLabel, ActionStatus.ACTIVE, e);
        }
    }
}
