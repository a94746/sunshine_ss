package com.vindie.sunshine_ss.common.record;

import com.vindie.sunshine_ss.common.dto.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo {
    private String uniqueId;
    private Language lang;
    private String appVersion;
}
