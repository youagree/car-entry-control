package ru.unit_techno.car_entry_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.unit_techno.car_entry_control.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
