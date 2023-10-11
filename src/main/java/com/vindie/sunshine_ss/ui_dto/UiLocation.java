package com.vindie.sunshine_ss.ui_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UiLocation {
    private Long id;
    private String name;
}
