package ru.unit_techno.car_entry_control.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.unit_techno.car_entry_control.entity.RfidLabel;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RfidLabelRepository extends JpaRepository<RfidLabel, Long>, JpaSpecificationExecutor<RfidLabel> {
    Optional<RfidLabel> findByRfidLabelValue(Long rfidLabelValue);

    @Modifying
    @Query("update RfidLabel set state = 'NEW' where rfidLabelValue = :rfidId")
    void resetRfidLabelStatus(@Param("rfidId") Long rfidId);

    @Modifying
    @Query("delete from RfidLabel where rfidLabelValue = :rfidId")
    void deleteByRfidLabelValue(@Param("rfidId") Long rfidLabelValue);

    @Modifying
    @Query("update RfidLabel set beforeActiveUntil = :dateBefore, noActiveUntil = :dateUntil, state = 'NO_ACTIVE' where rfidLabelValue = :rfidId")
    void deactivateRfidLabelUntilIntervalDate(@Param("dateBefore") LocalDate dateBefore, @Param("dateUntil") LocalDate until, @Param("rfidId") Long rfidId);

    @Modifying
    @Query("update RfidLabel set state = 'ACTIVE', noActiveUntil = NULL where noActiveUntil < current_timestamp and state = 'NO_ACTIVE'")
    void activateDeactivatedRfids();

    Page<RfidLabel> findAllByState (StateEnum state, Pageable sort);

    @Modifying
    @Query("update RfidLabel set state = 'NO_ACTIVE' where beforeActiveUntil < current_date")
    void deactivateRfidWhenHaveDeactDate();

    @Modifying
    @Query("update RfidLabel set state = 'ACTIVE', beforeActiveUntil = NULL, noActiveUntil = NULL where rfidLabelValue = :rfidId")
    void resumeRfidLabelState(@Param("rfidId") Long rfidId);
}
