package com.vindie.sunshine_ss.account.dto;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "creads")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "pass", nullable = false)
    private String pass;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "cread")
    private Account owner;
}
