package com.vindie.sunshine_ss.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchProgress {
    private String uuid;
    private Status status;

    public enum Status {
        IN_PROGRESS,
        DONE,
        ERROR
    }
}
