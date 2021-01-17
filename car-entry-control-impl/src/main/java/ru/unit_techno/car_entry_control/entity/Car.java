package ru.unit_techno.car_entry_control.entity;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Data
@Table(name = "cars")
@SequenceGenerator(name = "squd_car_id_seq", sequenceName = "squd_car_id_seq")
public class Car {
    @Id
    @Column(name = "car_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "squd_car_id_seq")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="rfid_label_id")
    private RfidLabel rfidLabel;

    @Column(name = "car_on_the_territory")
    private Boolean carOnTheTerritory;
}
