package ru.unit_techno.car_entry_control.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import ru.unit.techno.ariss.log.action.lib.repository.EventRepository;
import ru.unit_techno.car_entry_control.repository.CarRepository;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import java.util.Objects;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class StartDataLoader implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final EventRepository repository;
    private final CarRepository carRepository;
    private final RfidLabelRepository rfidLabelRepository;

    @Override
    public void run(String... args) {
        repository.deleteAll();
        carRepository.deleteAll();
        rfidLabelRepository.deleteAll();

        Resource resource = new ClassPathResource("test-data-events.sql");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
        populator.setSqlScriptEncoding("UTF-8");
        populator.execute(Objects.requireNonNull(jdbcTemplate.getDataSource()));

        Resource carsResourse = new ClassPathResource("test-data-cars.sql");
        ResourceDatabasePopulator populatorCars = new ResourceDatabasePopulator(carsResourse);
        populatorCars.setSqlScriptEncoding("UTF-8");
        populatorCars.execute(Objects.requireNonNull(jdbcTemplate.getDataSource()));

        Resource rfidResource = new ClassPathResource("test-data-rfids.sql");
        ResourceDatabasePopulator populatorRfids = new ResourceDatabasePopulator(rfidResource);
        populatorRfids.setSqlScriptEncoding("UTF-8");
        populatorRfids.execute(Objects.requireNonNull(jdbcTemplate.getDataSource()));

        log.info("Start data uploaded!");
    }
}
