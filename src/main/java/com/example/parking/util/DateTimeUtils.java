package com.example.parking.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Aides de manipulation des dates utilisees par les statistiques. */
public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /** Borne haute exclusive : debut du jour suivant. */
    public static LocalDateTime endOfDayExclusive(LocalDate date) {
        return date.plusDays(1).atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }

    /** Formate une duree en minutes sous la forme "2 j 03 h 15 min". */
    public static String formatDuration(long minutes) {
        if (minutes <= 0) {
            return "0 min";
        }
        long days = minutes / 1440;
        long hours = (minutes % 1440) / 60;
        long mins = minutes % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" j ");
        }
        if (days > 0 || hours > 0) {
            sb.append(hours).append(" h ");
        }
        sb.append(mins).append(" min");
        return sb.toString().trim();
    }
}
