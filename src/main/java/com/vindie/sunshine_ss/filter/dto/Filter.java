package com.vindie.sunshine_ss.filter.dto;

import com.vindie.sunshine_ss.account.dto.Account;
import com.vindie.sunshine_ss.common.dto.ChatPref;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "filters")
@Data
public class Filter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "filter")
    private Account owner;

    @Column(name = "age_from", nullable = false)
    private Byte ageFrom;

    @Column(name = "age_to", nullable = false)
    private Byte ageTo;

    @ToString.Exclude
    @OneToMany(mappedBy = "filter", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<RelationWithGenders> relationsWithGenders;

    @ElementCollection(targetClass = ChatPref.class, fetch = FetchType.EAGER)
    @JoinTable(name = "filters_to_chat_prefs", joinColumns = @JoinColumn(name = "filter_id"))
    @Column(name = "chat_prefs", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Set<ChatPref> chatPrefs;
}
