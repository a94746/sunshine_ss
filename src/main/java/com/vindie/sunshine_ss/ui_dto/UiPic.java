package com.vindie.sunshine_ss.ui_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UiPic {

    private Long id;
    private LocalDateTime lastModified;
    private byte[] file;

    public void validate() {
        var message = "UiPic validate";
        assertTrue(id != null, message);
        assertTrue(lastModified != null, message);
        assertTrue(file != null, message);
    }

    public void validate2() {
        var message = "UiPic validate2";
        assertTrue(file != null, message);
    }
}
