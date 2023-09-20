package com.vindie.sunshine_ss.match;

import com.vindie.sunshine_ss.account.dto.Account;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    private Account partner;

    @CreatedDate
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "viewed", nullable = false)
    private Boolean viewed;

    @Column(name = "liked", nullable = false)
    private Boolean liked;

    @Column(name = "actual", nullable = false)
    private Boolean actual;

    @Column(name = "message")
    private String message;
}
