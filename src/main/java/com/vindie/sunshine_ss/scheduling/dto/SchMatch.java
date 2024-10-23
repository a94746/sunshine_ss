package com.vindie.sunshine_ss.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchMatch {

    private long firstAccountId;
    private long secondAccountId;

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SchMatch))
            return false;
        SchMatch other = (SchMatch) o;
        return (this.firstAccountId == other.firstAccountId && this.secondAccountId == other.secondAccountId)
                || (this.firstAccountId == other.secondAccountId && this.secondAccountId == other.firstAccountId);
    }

    @Override
    public final int hashCode() {
        return (int) (firstAccountId + secondAccountId);
    }
}
