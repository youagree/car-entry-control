package ru.unit_techno.car_entry_control.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.unit.techno.ariss.barrier.api.dto.BarrierRequestDto;
import ru.unit.techno.device.registration.api.dto.DeviceResponseDto;

@Mapper
public interface EntryDeviceToReqRespMapper {

    @Mappings({
            @Mapping(source = "deviceId", target = "barrierId"),
            @Mapping(source = "entryAddress", target = "barrierCoreAddress")
    })
    BarrierRequestDto EntryDeviceToRequest(DeviceResponseDto responseDto);
}
