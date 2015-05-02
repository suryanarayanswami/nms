package org.motechproject.nms.outbounddialer.domain;

import org.joda.time.DateTime;

public enum DayOfTheWeek {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;

    public static DayOfTheWeek today() {
        return values()[DateTime.now().getDayOfWeek() - 1];
    }
}
