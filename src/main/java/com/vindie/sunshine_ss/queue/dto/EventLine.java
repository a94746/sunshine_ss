package com.vindie.sunshine_ss.queue.dto;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entries in this table should be entered manually
 * The timer processes them and creates a new table for distribution to users
 */
@Entity
@Table(name = "event_lines")
@Data
public class EventLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false, length = 1010)
    private String text;

    @Column(name = "notification", nullable = false)
    private Boolean notification; // either notification or opening dialog

    @Column(name = "opening_dialog", nullable = false)
    private Boolean openingDialog; // either notification or opening dialog

    @Column(name = "owner_id")
    private Long ownerId; // either owner or location

    @Column(name = "location_id")
    private Long locationId; // either owner or location

    @Column(name = "processed", nullable = false)
    private Boolean processed;
}
