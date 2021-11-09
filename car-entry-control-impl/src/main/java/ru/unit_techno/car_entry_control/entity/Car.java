package ru.unit_techno.car_entry_control.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "cars")
@SequenceGenerator(name = "squd_car_id_seq", sequenceName = "squd_car_id_seq")
public class Car {
    @Id
    @Column(name = "car_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "squd_car_id_seq")
    private Long id;

    @OneToOne(mappedBy = "car")
    private RfidLabel rfidLabel;

    @Column(name = "government_number")
    private String governmentNumber;

    @Column(name = "car_model")
    private String carModel;

    @Column(name = "car_color")
    private String carColor;

    @Column(name = "car_on_the_territory")
    private Boolean carOnTheTerritory;
}
