package com.vindie.sunshine_ss.account.dto;

import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Language;
import com.vindie.sunshine_ss.location.Location;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@SQLDelete(sql = "UPDATE accounts SET deleted = true WHERE id=?")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_presence", nullable = false)
    private LocalDateTime lastPresence;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 1010)
    private String description;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "bday", nullable = false)
    private LocalDate bday;

    @CreatedDate
    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "lang", nullable = false)
    private Language lang;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    /**
     * at the last scheduling, he participated in it, but nothing was found according to his filters
     */
    @Column(name = "deleted", nullable = false)
    private Boolean withoutActualMatches;



    @Column(name = "likes", nullable = false)
    private Integer likes;

    @Column(name = "views", nullable = false)
    private Integer views;

    @Column(name = "rating", nullable = false)
    private Byte rating;



    @Column(name = "permanent_matches_num", nullable = false)
    private Byte permanentMatchesNum;

    @Column(name = "temporary_matches_num")
    private Byte temporaryMatchesNum;

    @Column(name = "temporary_matches_till")
    private LocalDateTime temporaryMatchesTill;
}
