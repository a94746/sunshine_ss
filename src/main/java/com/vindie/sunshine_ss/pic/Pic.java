package com.vindie.sunshine_ss.pic;

import com.vindie.sunshine_ss.account.dto.Account;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "pics")
@Data
public class Pic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @Lob
    @Column(name = "file")
    private byte[] file;

    @Column(name = "num", nullable = false)
    private Byte num;


}
