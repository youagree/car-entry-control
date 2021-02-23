/*
 * VTB Group. Do not reproduce without permission in writing.
 * Copyright (c) 2021 VTB Group. All rights reserved.
 */

package ru.unit_techno.car_entry_control.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.unit_techno.car_entry_control.dto.CarCreateDto;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.mapper.CarMapper;
import ru.unit_techno.car_entry_control.repository.CarRepository;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarMapper carMapper;
    private final CarRepository carRepository;

    public void create(CarCreateDto carCreateDto) {
        Car car = carMapper.toDomain(carCreateDto);
        carRepository.save(car);
    }
}