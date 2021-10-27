package ru.unit_techno.car_entry_control.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class RfidActivationScheduler {

    private final RfidLabelRepository rfidLabelRepository;

    @Scheduled(cron = "0 */5 * * * *", zone = "")
    @Transactional
    public void activateDeactivatedRfids() {
        log.info("start activate rfids with status NO_ACTIVE");
        rfidLabelRepository.activateDeactivatedRfids();
    }

    @Scheduled(cron = "0 */10 * * * *", zone = "")
    @Transactional
    public void deactivateBlockedRfid() {
        log.info("start deactivateRfidWhenHaveDeactivateDate");
        rfidLabelRepository.deactivateRfidWhenHaveDeactDate();
    }
}
