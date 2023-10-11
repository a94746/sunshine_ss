package com.vindie.sunshine_ss.ui_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UiPicInfo {
    private Long id;
    private LocalDateTime lastModified;
}
