
package ru.unit_techno.car_entry_control.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.unit_techno.car_entry_control.entity.Event;
import ru.unit_techno.car_entry_control.repository.EventRepository;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LogRfidImpl implements LogAction<ActionObject> {

    private final DeviceEventConfig deviceEventConfig;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public void logSuccessAction(ActionObject actionObject) {
        Map<Long, MetaObject> type = deviceEventConfig.getType();
        //TODO при отсутствии метадаты по девайсу засетить фейковую метадату с НЕИЗВЕСТНОЕ УСТРОЙСТВО
        MetaObject metaObject = type.get(actionObject.getDeviceId());


        eventRepository.save(new Event()
                        .setEntryDeviceValue(actionObject.getDeviceId())
                        .setEventTime(actionObject.getEventTime())
                        .setEventType(metaObject.getEntryType().getValue())
                        .setRfidLabelValue(actionObject.getRfidLabelValue())
                        .setGosNumber(actionObject.getGosNumber())
                        .setInfo(metaObject.getInfo())
                        .setStateOfAction(actionObject.getActionStatus().getValue())
                        .setDescription(actionObject.getDescription())
                        .setErrored(actionObject.getIsErrored())
                //info, gosnumber
        );
    }

    @Override
    public void logExceptionObject(ActionObject actionObject) {

        eventRepository.save(new Event()
                        .setEntryDeviceValue(actionObject.getDeviceId())
                        .setEventTime(actionObject.getEventTime())
                        .setEventType(null)
                        .setRfidLabelValue(actionObject.getRfidLabelValue())
                        .setGosNumber(actionObject.getGosNumber())
                        .setInfo(null)
                        .setStateOfAction(actionObject.getActionStatus().getValue())
                //info, gosnumber
        );
    }
}