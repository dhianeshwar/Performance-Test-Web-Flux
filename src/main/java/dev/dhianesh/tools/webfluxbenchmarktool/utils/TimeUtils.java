package dev.dhianesh.tools.webfluxbenchmarktool.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtils {

    public static int secondsToMilliseconds(int seconds) {
        return seconds * 1000;
    }
}
