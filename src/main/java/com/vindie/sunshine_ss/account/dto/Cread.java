package com.vindie.sunshine_ss.account.dto;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "creads")
@Data
public class Cread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "pass", nullable = false)
    private String pass;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    @Column(name = "email_code")
    private byte emailCode;
}
