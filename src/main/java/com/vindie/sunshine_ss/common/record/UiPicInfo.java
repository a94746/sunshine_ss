package com.vindie.sunshine_ss.common.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UiPicInfo {
    private Long id;
    private LocalDateTime lastModified;
}
