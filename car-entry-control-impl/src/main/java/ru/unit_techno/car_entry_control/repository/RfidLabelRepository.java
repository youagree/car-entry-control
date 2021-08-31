package ru.unit_techno.car_entry_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;

import java.util.Optional;

@Repository
public interface RfidLabelRepository extends JpaRepository<RfidLabel, Long> {
    Optional<RfidLabel> findByRfidLabelValue(Long rfidLabelValue);

    @Modifying
    @Query("update RfidLabel set state = 'NEW' where rfidLabelValue = :rfidId")
    void resetRfidLabelStatus(@Param("rfidId") Long rfidId);

    @Modifying
    @Query("delete from RfidLabel where rfidLabelValue = :rfidId")
    void deleteByRfidLabelValue(@Param("rfidId") Long rfidLabelValue);
}
