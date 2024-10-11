package com.vindie.sunshine_ss.common.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.Integer.parseInt;

@Service
@Slf4j
@AllArgsConstructor
public class VersionUtils {
    public static final String ANDROID_POSTFIX = "a";
    public static final String IOS_POSTFIX = "i";

    private final PropertiesService properties;

    public boolean isRelevantVersion(String versionOs) {
        final String os = getPostfix(versionOs);

        final String relevantVersionNum = switch (os) {
            case ANDROID_POSTFIX -> properties.androidLeastVersion;
            case IOS_POSTFIX -> properties.iosLeastVersion;
            default -> throw new IllegalArgumentException("Unknown os app version " + versionOs);
        };
        return isRelevantVersion(getWithoutPostfix(versionOs), getWithoutPostfix(relevantVersionNum));
    }

    private boolean isRelevantVersion(String versionNum, String relevantVersionNum) {
        final String[] version = versionNum.split("\\.");
        final String[] relevantVersion = relevantVersionNum.split("\\.");

        if (version.length != relevantVersion.length)
            throw new IllegalArgumentException(
                    "Unknown length of app version: " + versionNum + ", relevantVersion: " + relevantVersionNum);

        for (int i = 0; i < version.length; i++) {
            if (parseInt(version[i]) < parseInt(relevantVersion[i]))
                return false;
            if (parseInt(version[i]) > parseInt(relevantVersion[i]))
                return true;
        }
        return true;
    }

    private String getWithoutPostfix(String version) {
        return version.substring(0, version.length() - 2);
    }

    private String getPostfix(String version) {
        return version.substring(version.length() - 1);
    }
}
