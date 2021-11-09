package ru.unit_techno.car_entry_control.service;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unit.techno.ariss.barrier.api.BarrierFeignClient;
import ru.unit.techno.ariss.barrier.api.dto.BarrierRequestDto;
import ru.unit.techno.ariss.barrier.api.dto.BarrierResponseDto;
import ru.unit.techno.ariss.log.action.lib.api.LogActionBuilder;
import ru.unit.techno.ariss.log.action.lib.config.DeviceEventConfig;
import ru.unit.techno.ariss.log.action.lib.entity.Description;
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
    private final BarrierFeignClient barrierFeignClient;
    private final EntryDeviceToReqRespMapper reqRespMapper;
    private final LogActionBuilder logActionBuilder;
    private final DeviceEventConfig eventConfig;

    @Transactional
    public String rfidLabelCheck(RfidEntry rfidLabel) {
        Long barrierId = 0L;
        Long longRfidLabel = rfidLabel.getRfid();
        log.info("rfid id is: {}", longRfidLabel);
        /// TODO: 21.10.2021 Разобраться как в этот объект передать гос номер для логирования эвернта в случае успешного поиска рфид метки, но она не активна
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
            BarrierResponseDto barrierResponse = barrierFeignClient.openBarrier(barrierRequest);

            //TODO сделать проверку пришедшего статуса!
            log.debug("ARISS BARRIER MODULE RESPONSE: {}", barrierResponse);
            log.debug("finish validate rfid, start open entry device");
            logActionBuilder.buildActionObjectAndLogAction(rfidLabel.getDeviceId(),
                    rfidLabel.getRfid(),
                    rfid.getCar().getGovernmentNumber(),
                    ActionStatus.ACTIVE);

            return rfid.getRfidLabelValue().toString();
        } catch (EntityNotFoundException e) {
            log.error("unknown barrier", e);
            MetaObject metaObject = eventConfig.getType().get(barrierId);
            notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo());
            catchAction(rfidLabel, rfid, ActionStatus.UNKNOWN, e);
            return "";
        } catch (RfidAccessDeniedException e) {
            // TODO: 03.08.2021 написать тест и проверить то заполняется гос номер
            MetaObject metaObject = eventConfig.getType().get(barrierId);
            notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo());
            log.error("unknown barrier", e);
            catchAction(rfidLabel, rfid, ActionStatus.STOP, e);
            return "";
        } catch (FeignException e) {
            MetaObject metaObject = eventConfig.getType().get(barrierId);
            notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo());
            log.error("Service not available", e);
            catchActionWhenFeignException(rfidLabel, rfid, ActionStatus.ACTIVE, e);
            return "";
        } catch (Exception e) {
            MetaObject metaObject = eventConfig.getType().get(barrierId);
            notificationService.sendActiveButSomethingUnavailable(metaObject.getInfo());
            log.error("exception when try to open barrier", e);
            catchAction(rfidLabel, rfid, ActionStatus.UNKNOWN, e);
            return "";
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

    private void catchAction(RfidEntry rfidLabel,
                             RfidLabel rfid,
                             ActionStatus actionStatus,
                             Exception e) {
        logActionBuilder.buildActionObjectAndLogAction(
                rfidLabel.getDeviceId(),
                rfidLabel.getRfid(),
                rfid.getCar().getGovernmentNumber(),
                actionStatus,
                true,
                new Description()
                        .setStatusCode("500")
                        .setMessage(e.getMessage()));
    }

    private void catchActionWhenFeignException(RfidEntry rfidLabel,
                                               RfidLabel rfid,
                                               ActionStatus actionStatus,
                                               FeignException e) {
        logActionBuilder.buildActionObjectAndLogAction(
                rfidLabel.getDeviceId(),
                rfidLabel.getRfid(),
                rfid.getCar().getGovernmentNumber(),
                actionStatus,
                true,
                new Description().setStatusCode(String.valueOf(e.status()))
                        .setMessage(e.getMessage()).setErroredServiceName(e.request().url()));
    }
}
