
package ru.unit_techno.car_entry_control.config;

import lombok.RequiredArgsConstructor;
import ru.unit_techno.car_entry_control.entity.Event;
import ru.unit_techno.car_entry_control.repository.EventRepository;

import java.util.Map;

@RequiredArgsConstructor
public class LogRfidImpl implements LogAction<ActionObject> {

    private final DeviceEventConfig deviceEventConfig;
    private final EventRepository eventRepository;

    @Override
    public void logSuccessAction(ActionObject actionObject) {
        Map<Long, MetaObject> type = deviceEventConfig.getType();
        MetaObject metaObject = type.get(actionObject.getDeviceId());


        eventRepository.save(new Event()
                        .setEntryDeviceValue(actionObject.getDeviceId())
                        .setEventTime(actionObject.getEventTime())
                        .setEventType(metaObject.getEntryType().getValue())
                        .setRfidLabelValue(actionObject.getRfidLabelValue())
                //info, gosnumber
        );
    }

    @Override
    public void logExceptionObject(ActionObject actionObject) {

    }
}