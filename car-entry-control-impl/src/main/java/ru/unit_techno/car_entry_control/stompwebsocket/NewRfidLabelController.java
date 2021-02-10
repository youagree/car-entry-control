package ru.unit_techno.car_entry_control.stompwebsocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import ru.unit_techno.car_entry_control.dto.NewRfidLabelMessage;

@RestController("v1")
public class NewRfidLabelController {

    @MessageMapping("/subscribe")
    @SendTo("/topic/newrfidlabel")
    public NewRfidLabelMessage notify(Long id) {
        return new NewRfidLabelMessage().setId(id);
    }
}
