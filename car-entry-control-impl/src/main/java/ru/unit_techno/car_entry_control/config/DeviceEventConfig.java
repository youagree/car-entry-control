
package ru.unit_techno.car_entry_control.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties("event")
public class DeviceEventConfig {

    @Getter
    private Map<Long, MetaObject> type;
}