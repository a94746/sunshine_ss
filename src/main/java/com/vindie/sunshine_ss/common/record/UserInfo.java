package com.vindie.sunshine_ss.common.record;

import com.vindie.sunshine_ss.common.dto.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.springframework.util.StringUtils.hasLength;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo {
    private String uniqueId;
    private Language lang;
    private String appVersion;

    public void validate() {
        var message = "UserInfo validate";
        assertTrue(hasLength(uniqueId), message);
        assertTrue(lang != null, message);
        assertTrue(hasLength(appVersion), message);
    }
}
