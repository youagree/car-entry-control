package ru.unit_techno.car_entry_control.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.unit_techno.car_entry_control.dto.CardsWithRfidLabelsDto;
import ru.unit_techno.car_entry_control.dto.RfidLabelDto;
import ru.unit_techno.car_entry_control.entity.RfidLabel;

@Mapper
public interface RfidMapper {
    RfidLabelDto toDto(RfidLabel label);

    @Mappings({
            @Mapping(source = "label.car.carColour", target = "carColor"),
            @Mapping(source = "label.car.governmentNumber", target = "governmentNumber"),
            @Mapping(source = "label.car.carModel", target = "carModel")
    })
    CardsWithRfidLabelsDto toDtoWithCars (RfidLabel label);
}
