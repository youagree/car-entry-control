/*
 * VTB Group. Do not reproduce without permission in writing.
 * Copyright (c) 2021 VTB Group. All rights reserved.
 */

package ru.unit_techno.car_entry_control.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unit_techno.car_entry_control.dto.CarCreateDto;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.mapper.CarMapper;
import ru.unit_techno.car_entry_control.repository.CarRepository;

import javax.persistence.EntityExistsException;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarMapper carMapper;
    private final CarRepository carRepository;

    @Transactional
    public void create(CarCreateDto carCreateDto) {
        validateGovernmentNumber(carCreateDto.getGovernmentNumber());
        Car car = carMapper.toDomain(carCreateDto);
        carRepository.save(car);
    }

    public void validateGovernmentNumber(String governmentNumber) {
        if (!governmentNumber.matches("^[АВЕКМНОРСТУХ]\\d{3}[АВЕКМНОРСТУХ]{2} \\d{2,3}$")) {
            throw new IllegalArgumentException("Invalid government number: " + governmentNumber + ". Please try again!");
        }

        if (carRepository.findCarByGovernmentNumber(governmentNumber).isPresent()) {
            throw new EntityExistsException("This government number " + governmentNumber + " is already exist! Please try again!");
        }
    }
}