package com.vindie.sunshine_ss.ui_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifRecord {
    private List<Long> ids;
    private String title;
    private String text;
}
