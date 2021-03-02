
package ru.unit_techno.car_entry_control.mapper;

import org.mapstruct.Mapper;
import ru.unit_techno.car_entry_control.dto.CarCreateDto;
import ru.unit_techno.car_entry_control.entity.Car;

@Mapper
public interface CarMapper {

    Car toDomain(CarCreateDto carCreateDto);
}