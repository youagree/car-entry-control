package ru.unit_techno.car_entry_control.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@Table(name = "rfid_labels")
@SequenceGenerator(name = "squd_rfid_label_id_seq", sequenceName = "squd_rfid_label_id_seq")
public class RfidLabel {
    @Id
    @Column(name = "rfid_label_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "squd_rfid_label_id_seq")
    private Long id;

    @Column(name = "rfid_label_value")
    private Long rfidLabelValue;

    @Column
    @Enumerated(EnumType.STRING)
    private StateEnum state;

    @Column(name = "no_active_until")
    private LocalDate noActiveUntil;

    @Column(name = "before_active_until")
    private LocalDate beforeActiveUntil;

    @OneToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "creation_date")
    private Timestamp creationDate;
}
