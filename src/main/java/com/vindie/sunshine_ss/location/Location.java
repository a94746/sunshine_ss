package com.vindie.sunshine_ss.location;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
@Data
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_en", nullable = false, unique = true)
    private String nameEn;

    @Column(name = "name_ru", nullable = false, unique = true)
    private String nameRu;

    @Column(name = "time_shift", nullable = false)
    private Byte timeShift;

    @Column(name = "scheduled_now", nullable = false)
    private Boolean scheduledNow;

    @Column(name = "last_scheduling", nullable = false)
    private LocalDateTime lastScheduling;
}
