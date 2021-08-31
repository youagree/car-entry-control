package ru.unit_techno.car_entry_control.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.unit_techno.car_entry_control.dto.CarCreateDto;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.repository.CarRepository;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;
import ru.unit_techno.car_entry_control.service.CarService;
import ru.unit_techno.car_entry_control.service.EventService;
import ru.unit_techno.car_entry_control.service.RfidService;

@Component
@RequiredArgsConstructor
public class InitRfidLabelData implements CommandLineRunner {

    private final RfidLabelRepository rfidLabelRepository;
    private final CarRepository carRepository;
    private final CarService carService;
    private final RfidService rfidService;
    private final EventService eventService;

    @Override
    public void run(String... args) throws Exception {
        carRepository.deleteAll();
        rfidLabelRepository.deleteAll();

        carService.create(new CarCreateDto().setCarColour("Zeleniy").setCarModel("Zhighoull").setGovernmentNumber("А777АА 777"));
        eventService.create(12345L);
        rfidService.fillBlankRfidLabel(12345L, "А777АА 777");
    }
}
