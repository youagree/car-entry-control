package ru.unit_techno.car_entry_control.aspect;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.unit_techno.car_entry_control.entity.Event;
import ru.unit_techno.car_entry_control.repository.EventRepository;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@Data
public class EventLogger {

    private final EventRepository eventRepository;

    @Autowired
    public EventLogger(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    //TODO сделать прием любых аргументов и на паблик методы
    @Pointcut("@annotation(RfidEvent) && args(rfidLabel,..)")
    public void rfidEvent(Long rfidLabel) {
    }

    @After(value = "rfidEvent(rfidLabel)", argNames = "rfidLabel")
    public void logRfidEvent(Long rfidLabel) {
        Event event = new Event();
        event.setRfidLabelValue(rfidLabel);
        //TODO завести энам с эвент тайпами
        event.setEventType("New rfid created");
        event.setEventTime(LocalDateTime.now());
        event.setEntryDeviceValue(1L);
        eventRepository.save(event);
    }
}
