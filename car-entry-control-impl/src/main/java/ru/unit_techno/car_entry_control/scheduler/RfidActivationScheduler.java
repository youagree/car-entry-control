package ru.unit_techno.car_entry_control.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RfidActivationScheduler {

    private final RfidLabelRepository rfidLabelRepository;

    @Scheduled(cron = "0 */5 * * * *", zone = "")
    @Transactional
    public void activateDeactivatedRfids() {
        rfidLabelRepository.activateDeactivatedRfids();
    }
}
