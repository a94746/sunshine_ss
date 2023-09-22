package com.vindie.sunshine_ss.queue.dto;

import jakarta.persistence.*;
import lombok.Data;

/**
 * записи в эту таблицу мы заносим вручную
 * Таймер их обрабатывает и создаёт новую таблицу для рассылки по пользователям
 */
@Entity
@Table(name = "event_lines")
@Data
public class EventLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false, length = 1010)
    private String text;

    @Column(name = "notification", nullable = false)
    private Boolean notification; // или уведомление по сокету или диалог при открытии приложения

    @Column(name = "opening_dialog", nullable = false)
    private Boolean openingDialog; // или уведомление по сокету или диалог при открытии приложения

    @Column(name = "owner_id")
    private Long ownerId; // или овнер или локейшн

    @Column(name = "location_id")
    private Long locationId; // или овнер или локейшн

    @Column(name = "processed", nullable = false)
    private Boolean processed;
}
