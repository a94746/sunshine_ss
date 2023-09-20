package com.vindie.sunshine_ss.filter;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.common.dto.OnlinePref;
import com.vindie.sunshine_ss.common.dto.Relation;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "account_filters")
@Data
public class Filter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private Account owner;

    @Column(name = "age_from", nullable = false)
    private Byte ageFrom;

    @Column(name = "age_to", nullable = false)
    private Byte ageTo;

    @ElementCollection(targetClass = Gender.class)
    @JoinTable(name = "filters_to_genders", joinColumns = @JoinColumn(name = "filter_id"))
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Set<Gender> genders;

    @ElementCollection(targetClass = Relation.class)
    @JoinTable(name = "filters_to_relations", joinColumns = @JoinColumn(name = "filter_id"))
    @Column(name = "relation", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Set<Relation> relations;

    @ElementCollection(targetClass = Gender.class)
    @JoinTable(name = "filters_to_online_prefs", joinColumns = @JoinColumn(name = "filter_id"))
    @Column(name = "online_prefs", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Set<OnlinePref> onlinePrefs;
}
