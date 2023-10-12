package com.vindie.sunshine_ss.scheduling.rules;

import com.vindie.sunshine_ss.common.dto.Relation;
import com.vindie.sunshine_ss.scheduling.dto.SchAccount;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class SmalRules {

    private SmalRules(){}
    private static final BiPredicate<SchAccount, SchAccount> THE_SAME_ACC =
            (a1, a2) -> a1.getId() == a2.getId();
    public static final BiPredicate<SchAccount, SchAccount> NOT_THE_SAME_ACC = THE_SAME_ACC.negate();
    private static final BiPredicate<SchAccount, SchAccount> ONE_IN_AVOID_MATCHES =
            (a1, a2) -> a1.getAvoidMatches().contains(a2.getId());
    private static final BiPredicate<SchAccount, SchAccount> AT_LEAST_ONE_IN_AVOID_MATCHES =
            (a1, a2) -> ONE_IN_AVOID_MATCHES.test(a1, a2) || ONE_IN_AVOID_MATCHES.test(a2, a1);
    public static final BiPredicate<SchAccount, SchAccount> NUT_IN_AVOID_MATCHES = AT_LEAST_ONE_IN_AVOID_MATCHES.negate();
    public static final Predicate<SchAccount> ONE_ENOUGH_MATCHES = a -> a.getResultMatches().size() >= a.getMatchNum();
    public static final BiPredicate<SchAccount, SchAccount> TWO_NOT_ENOUGH_MATCHES =
            (a1, a2) -> ONE_ENOUGH_MATCHES.negate().test(a1) && ONE_ENOUGH_MATCHES.negate().test(a2);



    /**
     * the second one fits the preferences of the first one
     */
    private static final BiPredicate<SchAccount, SchAccount> ONE_SUITABLE_AGE =
            (a1, a2) -> a2.getAge() >= a1.getAgeFrom() && a2.getAge() <= a1.getAgeTo();
    public static final BiPredicate<SchAccount, SchAccount> TWO_SUITABLE_AGE =
            (a1, a2) -> ONE_SUITABLE_AGE.test(a1, a2) && ONE_SUITABLE_AGE.test(a2, a1);
    /**
     * the second one fits the preferences of the first one
     */
    public static final BiPredicate<SchAccount, SchAccount> TWO_SUITABLE_GENDER =
            (a1, a2) -> {
                Set<Relation> relations1 = a1.getGenders2relations().get(a2.getGender());
                if (relations1 == null) return false;
                Set<Relation> relations2 = a2.getGenders2relations().get(a1.getGender());
                if (relations2 == null) return false;
                return relations1.stream()
                        .anyMatch(relations2::contains);
            };



    private static final BiPredicate<SchAccount, Integer> ONE_SUITABLE_LAST_PRESENCE =
            (a1, hours) -> a1.getLastPresenceHours() <= hours;
    public static final BiPredicate<SchAccount, SchAccount> TWO_SUITABLE_LAST_PRESENCE =
            (a1, a2) -> {
                int lastPresenceLimit = SmalRules.AT_LEAST_ONE_PREM.test(a1, a2)
                        ? Flow.LAST_PRESENCE_LIMIT_HOURS_PREM
                        : Flow.LAST_PRESENCE_LIMIT_HOURS;
                return ONE_SUITABLE_LAST_PRESENCE.test(a1, lastPresenceLimit)
                        && ONE_SUITABLE_LAST_PRESENCE.test(a2, lastPresenceLimit);
            };



    public static final BiPredicate<SchAccount, SchAccount> TWO_SUITABLE_RAITING =
            (a1, a2) -> {
                final float difference = Math.max(a1.getRating(), a2.getRating())
                        - Math.min(a1.getRating(), a2.getRating());
                return SmalRules.AT_LEAST_ONE_PREM.test(a1, a2)
                        ? difference <= Flow.RATING_BOUND
                        : difference <= Flow.RATING_BOUND_PREM;
                    };



    private static final BiPredicate<SchAccount, SchAccount> AT_LEAST_ONE_PREM =
            (a1, a2) -> a1.isPrem() || a2.isPrem();
}
