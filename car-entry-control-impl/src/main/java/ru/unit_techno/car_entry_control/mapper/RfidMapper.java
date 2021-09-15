package ru.unit_techno.car_entry_control.mapper;

import org.mapstruct.Mapper;
import ru.unit_techno.car_entry_control.dto.RfidLabelDto;
import ru.unit_techno.car_entry_control.entity.RfidLabel;

@Mapper
public interface RfidMapper {
    RfidLabelDto toDto(RfidLabel label);
}
