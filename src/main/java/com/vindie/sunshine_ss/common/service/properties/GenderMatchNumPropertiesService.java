package com.vindie.sunshine_ss.common.service.properties;

import com.vindie.sunshine_ss.common.dto.Gender;
import com.vindie.sunshine_ss.scheduling.dto.SchGender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.vindie.sunshine_ss.common.service.properties.PropertiesService.validate;

@Service
public class GenderMatchNumPropertiesService {

    private final byte maleNotPrem;
    private final byte malePrem;
    private final byte femaleNotPrem;
    private final byte femalePrem;
    private final byte nonBinaryNotPrem;
    private final byte nonBinaryPrem;

    public GenderMatchNumPropertiesService(@Value("${rules.gender.match-num.male.not-prem}") Byte maleNotPrem,
                                           @Value("${rules.gender.match-num.male.prem}") Byte malePrem,
                                           @Value("${rules.gender.match-num.female.not-prem}") Byte femaleNotPrem,
                                           @Value("${rules.gender.match-num.female.prem}") Byte femalePrem,
                                           @Value("${rules.gender.match-num.non-binary.not-prem}") Byte nonBinaryNotPrem,
                                           @Value("${rules.gender.match-num.non-binary.prem}") Byte nonBinaryPrem) {
        validate(maleNotPrem, "maleNotPrem");
        this.maleNotPrem = maleNotPrem;

        validate(malePrem, "malePrem");
        this.malePrem = malePrem;

        validate(femaleNotPrem, "femaleNotPrem");
        this.femaleNotPrem = femaleNotPrem;

        validate(femalePrem, "femalePrem");
        this.femalePrem = femalePrem;

        validate(nonBinaryNotPrem, "nonBinaryNotPrem");
        this.nonBinaryNotPrem = nonBinaryNotPrem;

        validate(nonBinaryPrem, "nonBinaryPrem");
        this.nonBinaryPrem = nonBinaryPrem;
    }

    public byte getMatchMaxNum(SchGender gender, boolean isPremium) {
        return switch (gender) {
            case MALE -> isPremium
                    ? malePrem
                    : maleNotPrem;
            case FEMALE -> isPremium
                    ? femalePrem
                    : femaleNotPrem;
            case NON_BINARY -> isPremium
                    ? nonBinaryPrem
                    : nonBinaryNotPrem;
        };
    }

    public byte getMatchMaxNum(Gender gender, boolean isPremium) {
        return switch (gender) {
            case MALE -> isPremium
                    ? malePrem
                    : maleNotPrem;
            case FEMALE -> isPremium
                    ? femalePrem
                    : femaleNotPrem;
            case NON_BINARY -> isPremium
                    ? nonBinaryPrem
                    : nonBinaryNotPrem;
        };
    }
}
