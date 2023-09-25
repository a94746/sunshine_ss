package com.vindie.sunshine_ss.account.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "contacts")
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @Column(name = "k", nullable = false)
    private String key;

    @Column(name = "v", nullable = false)
    private String value;
}
