package com.vindie.sunshine_ss.account.dto;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Table(name = "devices")
@Data
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @CreatedDate
    @Column(name = "first_entry", nullable = false)
    private LocalDate firstEntry;

    @Column(name = "android_id")
    private String androidId;

    @Column(name = "imei")
    private String imei;

    @Column(name = "wifi_mac")
    private String wifiMac;

    @Column(name = "phone_num")
    private String phoneNum;
}
