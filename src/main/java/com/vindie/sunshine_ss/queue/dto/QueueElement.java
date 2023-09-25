package com.vindie.sunshine_ss.queue.dto;

import com.vindie.sunshine_ss.account.dto.Account;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "queue")
@Data
@EntityListeners(AuditingEntityListener.class)
public class QueueElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_line_id", nullable = false)
    private EventLine eventLine;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @CreatedDate
    @Column(name = "created", nullable = false)
    private LocalDate created;

}
