package org.setu.splitwise.Utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {

    public static LocalDateTime toLocalDateTime(long timestampMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.systemDefault());
    }

    public static LocalDateTime toLocalDateTime(Long timestampMillis) {
        return timestampMillis != null ?
                LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampMillis), ZoneId.systemDefault()) :
                LocalDateTime.now();
    }
}
