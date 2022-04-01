package ru.unit_techno.car_entry_control.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.unit_techno.car_entry_control.dto.response.BarrierUnavailable;
import ru.unit_techno.car_entry_control.dto.response.NewRfidLabelMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class WSNotificationService {

    private final SimpMessagingTemplate brokerMessagingTemplate;

    @Value("${ws.topics.not_active}")
    private String notActiveTopic;

    public void sendNotActive(Long rfidLabelValue, String notificationMessage) {
        log.info("send notification rfidLabelValue: {}, notMessage: {}", rfidLabelValue, notificationMessage);
        brokerMessagingTemplate.convertAndSend(
                notActiveTopic,
                new NewRfidLabelMessage()
                        .setMessage(notificationMessage)
                        .setRfidLabelValue(rfidLabelValue)
        );
    }

    public void sendActiveButSomethingUnavailable(String deviceMetaInfo, Long deviceId, String notificationMessage) {
        log.info("send notification metaInfo is {}, deviceId: {}, notMessage: {}", deviceMetaInfo, deviceMetaInfo, notificationMessage);
        brokerMessagingTemplate.convertAndSend(
                notActiveTopic,
                new BarrierUnavailable()
                        .setBarrierName(deviceMetaInfo)
                        .setDeviceId(deviceId)
                        .setNotificationMessage(notificationMessage)
        );
    }
}
