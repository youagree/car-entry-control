package ru.unit_techno.car_entry_control.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.unit_techno.car_entry_control.dto.response.NewRfidLabelMessage;

@Service
@RequiredArgsConstructor
public class WSNotificationService {

    private final SimpMessagingTemplate brokerMessagingTemplate;

    @Value("${ws.topics.not_active}")
    private String notActiveTopic;

    public void sendNotActive(Long rfidLabelValue) {
        brokerMessagingTemplate.convertAndSend(
                notActiveTopic,
                new NewRfidLabelMessage()
                        .setRfidLabelValue(rfidLabelValue)
        );
    }
}
