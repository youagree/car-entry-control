package ru.unit_techno.car_entry_control.service;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unit.techno.ariss.barrier.api.dto.BarrierRequestDto;
import ru.unit.techno.ariss.log.action.lib.config.DeviceEventConfig;
import ru.unit.techno.ariss.log.action.lib.model.ActionStatus;
import ru.unit.techno.ariss.log.action.lib.model.MetaObject;
import ru.unit.techno.device.registration.api.DeviceResource;
import ru.unit.techno.device.registration.api.dto.DeviceResponseDto;
import ru.unit.techno.device.registration.api.enums.DeviceType;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.Car;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.exception.custom.RfidAccessDeniedException;
import ru.unit_techno.car_entry_control.mapper.EntryDeviceToReqRespMapper;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class EventService {

    private final WSNotificationService notificationService;
    private final RfidLabelRepository rfidLabelRepository;
    private final DeviceResource deviceResource;
    private final EntryDeviceToReqRespMapper reqRespMapper;
    private final DeviceEventConfig eventConfig;
    private final ActionCatchService actionCatchService;
    private final BarrierFeignService barrierFeignService;

    @Transactional
    public void rfidLabelCheck(RfidEntry rfidLabel) {
        Long barrierId = 0L;
        Long longRfidLabel = rfidLabel.getRfid();
        log.info("rfid id is: {}", longRfidLabel);

        RfidLabel rfid = new RfidLabel().setCar(new Car().setGovernmentNumber(null)).setRfidLabelValue(longRfidLabel);
        Optional<RfidLabel> label = rfidLabelRepository.findByRfidLabelValue(longRfidLabel);
        try {
            rfid = rfidExceptionCheck(label);

            DeviceResponseDto entryDevice = deviceResource.getGroupDevices(rfidLabel.getDeviceId(), DeviceType.RFID);

            log.debug("ENTRY DEVICE FROM DEVICE-REGISTRATION-CORE: {}", entryDevice);
            BarrierRequestDto barrierRequest = reqRespMapper.entryDeviceToRequest(entryDevice);
            barrierId = barrierRequest.getBarrierId();
            barrierRequest.setGovernmentNumber(rfid.getCar().getGovernmentNumber());

            log.debug("SEND REQUEST TO ARISS BARRIER MODULE, REQUEST BODY: {}", barrierRequest);
            barrierFeignService.openBarrier(barrierRequest, rfid, rfidLabel);

            //TODO сделать проверку пришедшего статуса!
            log.debug("finish validate rfid, start open entry device");
        } catch (EntityNotFoundException e) {
            log.error("unknown barrier", e);
            MetaObject metaObject = eventConfig.getType().get(barrierId);
            notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo(), barrierId);
            actionCatchService.catchAction(rfidLabel, rfid, ActionStatus.UNKNOWN, e);
        } catch (RfidAccessDeniedException e) {
            // TODO: 03.08.2021 написать тест и проверить то заполняется гос номер
            MetaObject metaObject = eventConfig.getType().get(barrierId);
            notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo(), barrierId);
            log.error("unknown barrier", e);
            actionCatchService.catchAction(rfidLabel, rfid, ActionStatus.NO_ACTIVE, e);
        } catch (FeignException e) {
            MetaObject metaObject = eventConfig.getType().get(barrierId);
            notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo(), barrierId);
            log.error("Service not available", e);
            actionCatchService.catchActionWhenFeignException(rfidLabel, rfid, ActionStatus.ACTIVE, e);
        } catch (Exception e) {
            MetaObject metaObject = eventConfig.getType().get(barrierId);
            notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo(), barrierId);
            log.error("exception when try to open barrier", e);
            actionCatchService.catchAction(rfidLabel, rfid, ActionStatus.UNKNOWN, e);
        }
    }

    @Transactional
    public void create() {
        log.info("start create new rfid label");

        /// TODO: 19.10.2021 ПОТОМ ЗАПРАШИВАТЬ ИДЕНТИФИКАТОР РФИД МЕТКИ С ПРОШИВКИ
        Long onFirmware = new Random().nextLong();
        Optional<RfidLabel> foundedRfidLabel = rfidLabelRepository.findByRfidLabelValue(onFirmware);

        /// TODO: 09.11.2021 Докинуть эксепшены для ситуаций когда считыватель отъебнул и когда прошел таймаут

        if (foundedRfidLabel.isEmpty()) {
            RfidLabel newRfidLabel = new RfidLabel()
                    .setRfidLabelValue(onFirmware)
                    .setState(StateEnum.NEW);
            rfidLabelRepository.save(newRfidLabel);
            log.info("successfully create new rfid label, {}, status is NEW, you need to activate this rfid", newRfidLabel);
            return;
        }

        throw new EntityExistsException("rfid label is already exist");
    }

    @SneakyThrows
    private RfidLabel rfidExceptionCheck(Optional<RfidLabel> rfidLabel) {
        if (rfidLabel.isEmpty()) {
            log.info("rfidLabel is empty, not exist");
            //todo нотифекейшн попытка по неизвестной метке
            throw new EntityNotFoundException("this rfid label is not in the database");
        }

        if (rfidLabel.get().getState().equals(StateEnum.NO_ACTIVE) ||
                rfidLabel.get().getState().equals(StateEnum.NEW)) {
            log.info("rfidLabel is not active");
            notificationService.sendNotActive(rfidLabel.get().getRfidLabelValue());
            throw new RfidAccessDeniedException("this rfid label is not active");
        }

        return rfidLabel.get();
    }
}
