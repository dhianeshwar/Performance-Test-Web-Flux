package dev.dhianesh.tools.webfluxbenchmarktool.utils;

import lombok.experimental.UtilityClass;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class TimeUtils {

    public static int secondsToMilliseconds(int seconds) {
        return seconds * 1000;
    }

    /**
     * Converts a non-negative duration in milliseconds to the number of full minutes.
     *
     * @param milliseconds non-negative duration in milliseconds
     * @return full minutes contained in the input (truncated)
     * @throws IllegalArgumentException if milliseconds is negative
     */
    public static long toWholeMinutes(long milliseconds) {
        if (milliseconds < 0) {
            throw new IllegalArgumentException("Duration must be non-negative: " + milliseconds);
        }
        return TimeUnit.MILLISECONDS.toMinutes(milliseconds);
    }
}
