package ru.unit_techno.car_entry_control.entity;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "entry_devices")
@SequenceGenerator(name = "squd_entry_device_id_seq", sequenceName = "squd_entry_device_id_seq")
public class EntryDevice {
    @Id
    @Column(name = "entry_device_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "squd_entry_device_id_seq")
    private Long id;

    @Column(name = "entry_device_slug")
    private Long slug;

    @Column(name = "entry_device_ip_address")
    private String direction;

    @Column(name = "entry_device_ip_address")
    private String ipAddress;
}
