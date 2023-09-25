package com.vindie.sunshine_ss.match;

import com.vindie.sunshine_ss.account.dto.Account;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @ToString.Exclude
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

    @Column(name = "message")
    private String message;
}
