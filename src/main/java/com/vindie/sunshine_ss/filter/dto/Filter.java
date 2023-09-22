package com.vindie.sunshine_ss.filter.dto;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.common.dto.ChatPref;
import com.vindie.sunshine_ss.common.dto.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@Entity
@Table(name = "filters")
@Data
public class Filter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "filter")
    private Account owner;

    @Column(name = "age_from", nullable = false)
    private Byte ageFrom;

    @Column(name = "age_to", nullable = false)
    private Byte ageTo;

    @OneToMany(mappedBy = "filter", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<RelationWithGenders> relationsWithGenders;

    @ElementCollection(targetClass = Gender.class, fetch = FetchType.EAGER)
    @JoinTable(name = "filters_to_chat_prefs", joinColumns = @JoinColumn(name = "filter_id"))
    @Column(name = "chat_prefs", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Collection<ChatPref> chatPrefs;
}
