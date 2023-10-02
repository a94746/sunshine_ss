package com.vindie.sunshine_ss.account.dto;

import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.Language;
import com.vindie.sunshine_ss.filter.dto.Filter;
import com.vindie.sunshine_ss.location.Location;
import com.vindie.sunshine_ss.match.Match;
import com.vindie.sunshine_ss.pic.Pic;
import com.vindie.sunshine_ss.queue.dto.QueueElement;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "accounts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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
    private Boolean deleted;



    @Column(name = "likes", nullable = false)
    private Integer likes;

    @Column(name = "views", nullable = false)
    private Integer views;

    @Column(name = "rating", nullable = false)
    private Double rating;



    @Column(name = "matches_num", nullable = false)
    private Byte matchesNum;

    @Column(name = "prem_matches_num")
    private Byte premMatchesNum;

    @Column(name = "prem_till")
    private LocalDateTime premTill;


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "filter_id", unique = true)
    private Filter filter;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Collection<Contact> contacts = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "owner", cascade = CascadeType.PERSIST)
    private List<Device> devices = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "cread_id", nullable = false, unique = true)
    private Cread cread;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Collection<Pic> pics = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Collection<Match> matchesOwner = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "partner", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Collection<Match> matchesPartner = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private Collection<QueueElement> queueElements = new ArrayList<>();
}
