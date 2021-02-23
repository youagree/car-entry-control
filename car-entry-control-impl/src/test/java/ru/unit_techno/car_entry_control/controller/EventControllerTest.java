
package ru.unit_techno.car_entry_control.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;
import ru.unit_techno.car_entry_control.test_utils.IntegrationTest;
import ru.unit_techno.car_entry_control.test_utils.TestUtils;

import java.util.Objects;

@Slf4j
@IntegrationTest
public class EventControllerTest {

    public static final String BASE_URL = "/v1/";
    public static final String EVENT = BASE_URL + "event";

    @Autowired
    private RfidLabelRepository rfidLabelRepository;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeTestClass
    public void init() {
        Resource resource = new ClassPathResource("init-test.sql");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
        populator.execute(Objects.requireNonNull(jdbcTemplate.getDataSource()));
        log.info("Start data uploaded!");
    }

    @Test
    @DisplayName("Положительный кейс, когда метка существует и в статусе ACTIVE, -> шлагбаум открывается")
    public void eventHandlerGoodCase() {
        rfidLabelRepository.saveAndFlush(
                new RfidLabel()
                .setRfidLabelValue(124L)
                .setState(StateEnum.ACTIVE)
        );
        testUtils.invokePostApi(Void.class, EVENT, HttpStatus.OK, new RfidEntry().setRfid(124L));
    }
}