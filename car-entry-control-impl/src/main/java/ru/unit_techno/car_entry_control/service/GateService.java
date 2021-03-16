package ru.unit_techno.car_entry_control.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GateService {

    public void forceOpenGate() {
        log.info("ОТКРЫВАЮ ПРИНУДИТЕЛЬНО ШЛАГБАУМ!");
        //TODO Добавить метод на принудительное открытие шлагбаума прям через прошивку
    }
}
