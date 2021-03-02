package ru.unit_techno.car_entry_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.unit_techno.car_entry_control.entity.RfidLabel;

import java.util.Optional;

@Repository
public interface RfidLabelRepository extends JpaRepository<RfidLabel, Long> {
    Optional<RfidLabel> findByRfidLabelValue(Long rfidLabelValue);
}
