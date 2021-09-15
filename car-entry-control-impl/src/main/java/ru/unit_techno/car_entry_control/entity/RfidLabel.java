package ru.unit_techno.car_entry_control.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;

import javax.persistence.*;
import java.sql.Timestamp;

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
    private Timestamp noActiveUntil;

    @OneToOne
    @JoinColumn(name="car_id")
    private Car car;

    @Column(name = "creation_date")
    private Timestamp creationDate;
}
