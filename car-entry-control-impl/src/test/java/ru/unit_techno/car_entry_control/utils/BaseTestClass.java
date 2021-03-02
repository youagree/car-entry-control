
package ru.unit_techno.car_entry_control.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import ru.unit_techno.car_entry_control.repository.CarRepository;
import ru.unit_techno.car_entry_control.repository.EventRepository;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;
import ru.unit_techno.car_entry_control.test_utils.IntegrationTest;
import ru.unit_techno.car_entry_control.test_utils.TestUtils;

import java.util.Objects;

@Slf4j
@IntegrationTest
public class BaseTestClass {

    @Autowired
    protected TestUtils testUtils;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected CarRepository carRepository;

    @Autowired
    protected RfidLabelRepository rfidLabelRepository;

    @Autowired
    protected EventRepository eventRepository;

    @BeforeTestClass
    private void init() {
        Resource resource = new ClassPathResource("init-test.sql");
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(resource);
        resourceDatabasePopulator.execute(Objects.requireNonNull(jdbcTemplate.getDataSource()));
        log.info("Start data uploaded!");
    }

    @AfterTestClass
    private void end() {
        rfidLabelRepository.deleteAll();
        carRepository.deleteAll();
        eventRepository.deleteAll();
        rfidLabelRepository.flush();
        carRepository.flush();
        eventRepository.flush();
    }
}