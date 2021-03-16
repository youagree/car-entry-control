package ru.unit_techno.car_entry_control.aspect;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.stereotype.Component;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.Event;
import ru.unit_techno.car_entry_control.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.springframework.core.annotation.AnnotatedElementUtils.getMergedAnnotationAttributes;

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

    @AfterReturning("execution(public * ru.unit_techno.car_entry_control.service.*.*(..)) && @annotation(RfidEvent)")
    public void logRfidEvent(JoinPoint joinPoint) {
        AnnotationAttributes annotationAttributes = getMergedAnnotationAttributes(
                ((MethodSignature) joinPoint.getSignature()).getMethod(),
                RfidEvent.class
        );
        Object[] args = joinPoint.getArgs();
        Event event = new Event();
        if (args[0] instanceof RfidEntry) {
            event.setRfidLabelValue(((RfidEntry) args[0]).getRfid());
            event.setEntryDeviceValue(((RfidEntry) args[0]).getDeviceId());
        } else {
            event.setRfidLabelValue((Long) args[0]);
            event.setEntryDeviceValue(1L);
        }
        if (Objects.requireNonNull(annotationAttributes).get("value") != null) {
            event.setEventType(annotationAttributes.get("value").toString());
        }
        event.setEventTime(LocalDateTime.now());
        eventRepository.save(event);
    }
}
