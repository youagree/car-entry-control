package ru.unit_techno.car_entry_control.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unit.techno.ariss.barrier.api.BarrierFeignClient;
import ru.unit.techno.ariss.barrier.api.dto.BarrierRequestDto;
import ru.unit.techno.ariss.barrier.api.dto.BarrierResponseDto;
import ru.unit.techno.device.registration.api.DeviceResource;
import ru.unit.techno.device.registration.api.dto.DeviceResponseDto;
import ru.unit_techno.car_entry_control.aspect.RfidEvent;
import ru.unit_techno.car_entry_control.aspect.enums.RfidEventType;
import ru.unit_techno.car_entry_control.config.ActionObject;
import ru.unit_techno.car_entry_control.config.ActionStatus;
import ru.unit_techno.car_entry_control.config.LogRfidImpl;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.exception.custom.RfidAccessDeniedException;
import ru.unit_techno.car_entry_control.mapper.EntryDeviceToReqRespMapper;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
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
    private final LogRfidImpl logRfid;

    @Transactional
    @RfidEvent(value = RfidEventType.CHECK_RFID)
    public String rfidLabelCheck(RfidEntry rfidLabel) throws Exception {
        Long longRfidLabel = rfidLabel.getRfid();
        log.info("rfid id is: {}", longRfidLabel);
        Optional<RfidLabel> label = rfidLabelRepository.findByRfidLabelValue(longRfidLabel);
        rfidExceptionCheck(label);
        DeviceResponseDto entryDevice = deviceResource.getGroupDevices(rfidLabel.getDeviceId());
        log.info("ENTRY DEVICE FROM DEVICE-REGISTRATION-CORE: {}", entryDevice);
        BarrierRequestDto barrierRequest = reqRespMapper.EntryDeviceToRequest(entryDevice);
        log.info("SEND REQUEST TO ARISS BARRIER MODULE, REQUEST BODY: {}", barrierRequest);
        BarrierResponseDto barrierResponse = barrierFeignClient.openBarrier(barrierRequest);
        //TODO сделать проверку пришедшего статуса!
        log.info("ARISS BARRIER MODULE RESPONSE: {}", barrierResponse);
        log.info("finish validate rfid, start open entry device");

        logRfid.logSuccessAction(new ActionObject()
                .setActionStatus(ActionStatus.ACTIVE)
                .setEventTime(LocalDateTime.now())
                .setDeviceId(rfidLabel.getDeviceId())
                .setGosNumber(label.get().getCar().getGovernmentNumber()));

        //todo собрать эвент и сохранить
        return label.get().getRfidLabelValue().toString();
    }

    @RfidEvent(value = RfidEventType.CREATE_RFID_LABEL)
    public void create(Long rfidLabel) {
        log.info("start create new rfid label {}", rfidLabel);
        Optional<RfidLabel> foundedRfidLabel = rfidLabelRepository.findByRfidLabelValue(rfidLabel);
        if(foundedRfidLabel.isEmpty()) {
            RfidLabel newRfidLabel = new RfidLabel()
                    .setRfidLabelValue(rfidLabel)
                    .setState(StateEnum.NEW);
            rfidLabelRepository.save(newRfidLabel);
            log.info("successfully create new rfid label, {}, status is NEW, you need to activate this rfid" , newRfidLabel);
            return;
        }
        throw new EntityExistsException("rfid label is already exist");
    }

    private void rfidExceptionCheck(Optional<RfidLabel> rfidLabel) throws RfidAccessDeniedException {
        if (rfidLabel.isEmpty()) {
            log.info("rfidLabel is empty, not exist");
            throw new EntityNotFoundException("this rfid label is not in the database");
        }

        if (rfidLabel.get().getState().equals(StateEnum.NO_ACTIVE) ||
                rfidLabel.get().getState().equals(StateEnum.NEW)) {
            log.info("rfidLabel is not active");
            notificationService.sendNotActive(rfidLabel.get().getRfidLabelValue());
            throw new RfidAccessDeniedException("this rfid label is not active");
        }
    }
}
