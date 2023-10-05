package com.vindie.sunshine_ss.common.record;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangeEmail {
    private String newEmail;
    private Integer emailCode;
}
