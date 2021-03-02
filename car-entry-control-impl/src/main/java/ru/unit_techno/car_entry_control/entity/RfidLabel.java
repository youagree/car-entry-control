package ru.unit_techno.car_entry_control.entity;

import lombok.Data;
import ru.unit_techno.car_entry_control.entity.enums.StateEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Data
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

    @OneToOne(mappedBy = "rfidLabel")
    private Car car;
}
