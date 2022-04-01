package com.brontoblocks;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class InstantUtils {

    public static CalendarInsight getCalendarInsights(Instant instant) {
        var ldt = LocalDateTime.ofEpochSecond(instant.getEpochSecond(), 0, ZoneOffset.UTC);

        return new CalendarInsight(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth());
    }

    public static final class CalendarInsight {

        public CalendarInsight(int year, int month, int dayOfMonth) {
            this.year = year;
            this.month = month;
            this.dayOfMonth = dayOfMonth;
        }

        public final int year;
        public final int month;
        public final int dayOfMonth;
    }
}
