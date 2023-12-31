package com.vindie.sunshine_ss.account.dto;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "devices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Account owner;

    @CreatedDate
    @Column(name = "first_entry", nullable = false)
    private LocalDate firstEntry;

    @Column(name = "unique_id", nullable = false, unique = true)
    private String uniqueId;

    @Column(name = "imei")
    private String imei;

    @Column(name = "wifi_mac")
    private String wifiMac;

    @Column(name = "phone_num")
    private String phoneNum;

    @Column(name = "app_version", nullable = false)
    private String appVersion;

    @Column(name = "logout_owner_id")
    private Long logoutOwnerId;
}
