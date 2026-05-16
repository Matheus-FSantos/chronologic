package io.github.matheus_fsantos.chronologic_core.application.core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SuffixUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd:HH");

    private SuffixUtils() { }

    public static String getCurrentSuffix() { return LocalDateTime.now().format(FORMATTER); }
    public static String getPastSuffix() { return  LocalDateTime.now().minusHours(1).format(FORMATTER); }
}
