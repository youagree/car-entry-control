
package ru.unit_techno.car_entry_control.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.unit.techno.ariss.log.action.lib.repository.EventRepository;
import ru.unit_techno.car_entry_control.repository.CarRepository;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;
import ru.unit_techno.car_entry_control.test_utils.IntegrationTest;
import ru.unit_techno.car_entry_control.test_utils.TestUtils;

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

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TransactionTemplate txTemplate;

    private static final String DB_NAME = "unit_techno";
    public static String DB_URL = null;

    private static final PostgreSQLContainer postgresDB = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName(DB_NAME)
            .withUsername("postgres")
            .withPassword("postgres")
            .withExposedPorts(5432)
            .withClasspathResourceMapping("init-test.sql", "/docker-entrypoint-initdb.d/init.sql", BindMode.READ_ONLY);

    static {
        postgresDB.start();
        DB_URL = String.format("jdbc:postgresql://%s:%d/unit_techno?currentSchema=car_entry_control",
                postgresDB.getContainerIpAddress(),
                postgresDB.getFirstMappedPort());
    }

    @DynamicPropertySource
    static void dynamicSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> DB_URL);
    }

    @AfterEach
    public void end() {
        rfidLabelRepository.deleteAll();
        carRepository.deleteAll();
        eventRepository.deleteAll();
        rfidLabelRepository.flush();
        carRepository.flush();
        eventRepository.flush();
    }

    public void doInTransactionVoid(Runnable method) {
        txTemplate.execute(tx -> {
            method.run();
            return tx;
        });
    }
}