package ru.unit_techno.car_entry_control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"ru.unit_techno.user.model.impl.entity",
		"ru.unit_techno.user.model.impl.entity.enums",
		"ru.unit_techno.car_entry_control.entity",
		"ru.unit_techno.car_entry_control.entity.enums"})
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"ru.unit_techno.user.model.impl.repository", "ru.unit_techno.car_entry_control.repository"})
public class CarEntryControlApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarEntryControlApplication.class, args);
	}
}
