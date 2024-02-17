package com.jeff_media.simpleblockwhitelist.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationParser {

    private static final Pattern regex = Pattern.compile(
            "(\\d+(?:\\.\\d+)?)\\s*" + // Number part
                    "(" +
                    "ms|milli|millis|millisecond|milliseconds|" +
                    "t|tick|ticks|" +
                    "s|sec|secs|second|seconds|" +
                    "m|min|mins|minute|minutes|" +
                    "h|hour|hours|" +
                    "d|day|days|" +
                    "w|week|weeks" +
                    ")?",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern onlyNumbers = Pattern.compile("(\\d+(?:\\.\\d+)?)", Pattern.CASE_INSENSITIVE);

    public static long parseDurationToTicks(String input) {
        return parseDurationToMillis(input) / 50;
    }

    public static long parseDurationToMillis(String input) {
        if (onlyNumbers.matcher(input).matches()) {
            return (long) (Double.parseDouble(input) * 1000);
        }

        double totalSeconds = 0.0;

        Matcher matcher = regex.matcher(input);
        while (matcher.find()) {
            String value = matcher.group(1);
            String unit = matcher.group(2);
            double numericValue = Double.parseDouble(value);

            switch (unit.toLowerCase()) {
                case "ms":
                case "milli":
                case "millis":
                case "millisecond":
                case "milliseconds":
                    totalSeconds += numericValue;
                    break;
                case "t":
                case "tick":
                case "ticks":
                    totalSeconds += numericValue * 50;
                    break;
                case "s":
                case "sec":
                case "secs":
                case "second":
                case "seconds":
                    totalSeconds += numericValue * 1000;
                    break;
                case "m":
                case "min":
                case "mins":
                case "minute":
                case "minutes":
                    totalSeconds += numericValue * 60 * 1000;
                    break;
                case "h":
                case "hour":
                case "hours":
                    totalSeconds += numericValue * 3600 * 1000;
                    break;
                case "d":
                case "day":
                case "days":
                    totalSeconds += numericValue * 86400 * 1000;
                    break;
                case "w":
                case "week":
                case "weeks":
                    totalSeconds += numericValue * 604800 * 1000;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid unit: " + unit);
            }
        }

        return (long) totalSeconds;
    }
}
