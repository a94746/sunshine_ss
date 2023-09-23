package com.vindie.sunshine_ss.scheduling.rules;

import com.vindie.sunshine_ss.scheduling.dto.SchAccount;

import java.util.function.BiPredicate;

public class Flow {
    private Flow() {}
    public static final float RATING_BOUND = 40F;
    public static final float RATING_BOUND_PREM = 70F;
    public static final int LAST_PRESENCE_LIMIT_HOURS = 72;
    public static final int LAST_PRESENCE_LIMIT_HOURS_PREM = 120;

    public static final BiPredicate<SchAccount, SchAccount> FIRST =
            SmalRules.TWO_NOT_ENOUGH_MATCHES
                    .and(SmalRules.NOT_THE_SAME_ACC)
                    .and(SmalRules.NUT_IN_AVOID_MATCHES)
                    .and(SmalRules.TWO_SUITABLE_GENDER)
                    .and(SmalRules.TWO_SUITABLE_AGE)
                    .and(SmalRules.TWO_SUITABLE_LAST_PRESENCE)
                    .and(SmalRules.TWO_SUITABLE_RAITING);

}
