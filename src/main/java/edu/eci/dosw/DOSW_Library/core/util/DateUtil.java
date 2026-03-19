package edu.eci.dosw.DOSW_Library.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtil() {
    }

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static String format(LocalDate date) {
        if (date == null)
            return null;
        return date.format(FORMATTER);
    }

    public static LocalDate parse(String dateStr) {
        ValidationUtil.validateNotEmpty(dateStr, "Date string");
        return LocalDate.parse(dateStr, FORMATTER);
    }
}
