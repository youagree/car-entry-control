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
import ru.unit.techno.ariss.log.action.lib.entity.Description;
import ru.unit.techno.ariss.log.action.lib.model.ActionStatus;
import ru.unit.techno.device.registration.api.DeviceResource;
import ru.unit.techno.device.registration.api.dto.DeviceResponseDto;
import ru.unit_techno.car_entry_control.aspect.RfidEvent;
import ru.unit_techno.car_entry_control.aspect.enums.RfidEventType;
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

    @Transactional
    @RfidEvent(value = RfidEventType.CHECK_RFID)
    public String rfidLabelCheck(RfidEntry rfidLabel) {
        Long longRfidLabel = rfidLabel.getRfid();
        log.info("rfid id is: {}", longRfidLabel);
        RfidLabel rfid = new RfidLabel().setCar(new Car().setGovernmentNumber(null)).setRfidLabelValue(longRfidLabel);
        Optional<RfidLabel> label = rfidLabelRepository.findByRfidLabelValue(longRfidLabel);
        try {
            rfid = rfidExceptionCheck(label);

            DeviceResponseDto entryDevice = deviceResource.getGroupDevices(rfidLabel.getDeviceId());

            log.debug("ENTRY DEVICE FROM DEVICE-REGISTRATION-CORE: {}", entryDevice);
            BarrierRequestDto barrierRequest = reqRespMapper.entryDeviceToRequest(entryDevice);
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
            notificationService.sendActiveButSomethingUnavailable(longRfidLabel);
            catchAction(rfidLabel, rfid, ActionStatus.UNKNOWN, e);
            return "";
        } catch (RfidAccessDeniedException e) {
            // TODO: 03.08.2021 написать тест и проверить то заполняется гос номер
            notificationService.sendActiveButSomethingUnavailable(longRfidLabel);
            log.error("unknown barrier", e);
            catchAction(rfidLabel, rfid, ActionStatus.STOP, e);
            return "";
        } catch (FeignException e) {
            notificationService.sendActiveButSomethingUnavailable(longRfidLabel);
            log.error("Service not available", e);
            catchActionWhenFeignException(rfidLabel, rfid, ActionStatus.ACTIVE, e);
            return "";
        } catch (Exception e) {
            notificationService.sendActiveButSomethingUnavailable(longRfidLabel);
            log.error("exception when try to open barrier", e);
            catchAction(rfidLabel, rfid, ActionStatus.UNKNOWN, e);
            return "";
        }
    }

    @RfidEvent(value = RfidEventType.CREATE_RFID_LABEL)
    public void create(Long rfidLabel) {
        log.info("start create new rfid label {}", rfidLabel);
        Optional<RfidLabel> foundedRfidLabel = rfidLabelRepository.findByRfidLabelValue(rfidLabel);
        if (foundedRfidLabel.isEmpty()) {
            RfidLabel newRfidLabel = new RfidLabel()
                    .setRfidLabelValue(rfidLabel)
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
