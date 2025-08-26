package com.si.mindhealth.utils;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class TimeUtils {

    public static boolean isToday(Instant instant) {
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");

        LocalDate inputDate = instant.atZone(zone).toLocalDate();
        LocalDate today = LocalDate.now(zone);

        return inputDate.equals(today);
    }

    public static boolean isToday(Date date) {
        return isToday(date.toInstant());
    }
}
