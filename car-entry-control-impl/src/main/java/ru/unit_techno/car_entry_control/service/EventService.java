package ru.unit_techno.car_entry_control.service;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.unit.techno.ariss.barrier.api.dto.BarrierRequestDto;
import ru.unit.techno.ariss.log.action.lib.model.ActionStatus;
import ru.unit.techno.device.registration.api.DeviceResource;
import ru.unit.techno.device.registration.api.dto.DeviceResponseDto;
import ru.unit.techno.device.registration.api.enums.DeviceType;
import ru.unit_techno.car_entry_control.dto.request.RfidEntry;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;
import ru.unit_techno.car_entry_control.exception.custom.RfidAccessDeniedException;
import ru.unit_techno.car_entry_control.exception.custom.RfidScannerTimeoutException;
import ru.unit_techno.car_entry_control.mapper.EntryDeviceToReqRespMapper;
import ru.unit_techno.car_entry_control.repository.RfidLabelRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAspectJAutoProxy
public class EventService {

    private final RfidLabelRepository rfidLabelRepository;
    private final DeviceResource deviceResource;
    private final EntryDeviceToReqRespMapper reqRespMapper;
    private final BarrierFeignService barrierFeignService;
    private final CatchActionCommonService catchAction;

    @Transactional
    public void rfidLabelCheck(RfidEntry rfidLabel) {
        Long longRfidLabel = rfidLabel.getRfid();
        log.info("rfid id is: {}", longRfidLabel);
        Optional<RfidLabel> label = rfidLabelRepository.findByRfidLabelValue(longRfidLabel);
        log.info("rfid is: {}", label.get());
        try {
            rfidExceptionCheck(label);
            RfidLabel existRfid = label.get();

            DeviceResponseDto entryDevice = deviceResource.getGroupDevices(rfidLabel.getDeviceId(), DeviceType.RFID);

            log.debug("ENTRY DEVICE FROM DEVICE-REGISTRATION-CORE: {}", entryDevice);
            BarrierRequestDto barrierRequest = reqRespMapper.entryDeviceToRequest(entryDevice);
            barrierRequest.setGovernmentNumber(existRfid.getCar().getGovernmentNumber());

            log.debug("SEND REQUEST TO ARISS BARRIER MODULE, REQUEST BODY: {}", barrierRequest);
            barrierFeignService.openBarrier(barrierRequest, existRfid, rfidLabel);

            log.debug("finish validate rfid, start open entry device");
        } catch (EntityNotFoundException e) {
            catchAction.rfidNotFoundCatchAndSaveAction(rfidLabel, longRfidLabel, e);
            throw (e);
        } catch (RfidAccessDeniedException e) {
            catchAction.rfidAccessDeniedCatchAndSave(rfidLabel, label.get(), ActionStatus.NO_ACTIVE, e);
            throw (e);
        } catch (FeignException e) {
            catchAction.feignExceptionCheckAndSave(rfidLabel, label, e);
            throw (e);
        }
    }

    @Transactional
    @SneakyThrows
    public void create() {
        log.info("start create new rfid label");

        DeviceResponseDto entryDevice = deviceResource.getReaderDeviceId();

        var url = "http://" + entryDevice.getEntryAddress() + "/api/squd-core/rfid/create/" + entryDevice.getDeviceId();
        var request = HttpRequest.newBuilder()
                .GET()
                // TODO get device id from device reg core
                .uri(new URI(url))
                .build();

        HttpResponse<String> response = null;
        try {
            response = HttpClient
                    .newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            log.info("success get new rfid from core {}", response);
        } catch (Exception e) {
            log.info("response is {}", response);
            throw new RfidScannerTimeoutException("service is not working now");
        }

        if (response.statusCode() == 408) {
            throw new RfidScannerTimeoutException("service is not working now");
        }

        /// TODO: 09.11.2021 Докинуть эксепшены для ситуаций когда считыватель отъебнул и когда прошел таймаут
        Optional<RfidLabel> foundedRfidLabel = rfidLabelRepository
                .findByRfidLabelValue(Long.parseLong(response.body()));

        if (foundedRfidLabel.isEmpty()) {
            RfidLabel newRfidLabel = new RfidLabel()
                    .setRfidLabelValue(Long.parseLong(response.body()))
                    .setState(StateEnum.NEW)
                    .setCreationDate(new Timestamp(System.currentTimeMillis()));

            //todo add timestamp
            rfidLabelRepository.save(newRfidLabel);
            log.info("successfully create new rfid label, {}, status is NEW, you need to activate this rfid", newRfidLabel);
            return;
        }
        throw new EntityExistsException("rfid label is already exist");
    }

    @SneakyThrows
    @Transactional
    public RfidLabel rfidExceptionCheck(Optional<RfidLabel> rfidLabel) {
        if (rfidLabel.isEmpty()) {
            log.info("rdif label is {}", rfidLabel.get());
            log.info("rfidLabel is empty, not exist");
            //todo нотифекейшн попытка по неизвестной метке
            throw new EntityNotFoundException("this rfid label is not in the database");
        }

        if (rfidLabel.get().getState().equals(StateEnum.NO_ACTIVE) ||
                rfidLabel.get().getState().equals(StateEnum.NEW)) {
            log.info("rfidLabel is not active");
            throw new RfidAccessDeniedException("this rfid label is not active");
        }
        return rfidLabel.get();
    }
}
