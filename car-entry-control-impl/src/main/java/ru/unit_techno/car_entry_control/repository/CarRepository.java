
package ru.unit_techno.car_entry_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.unit_techno.car_entry_control.entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    Car findCarByGovernmentNumber(String governmentNumber);
}