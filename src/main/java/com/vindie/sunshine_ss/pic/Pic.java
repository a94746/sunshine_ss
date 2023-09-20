package com.vindie.sunshine_ss.pic;

import com.vindie.sunshine_ss.account.dto.Account;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pics")
@Data
public class Pic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @Lob
    @Column(name = "file", nullable = false)
    private byte[] file;

    @Column(name = "num", nullable = false)
    private Byte num;
}
