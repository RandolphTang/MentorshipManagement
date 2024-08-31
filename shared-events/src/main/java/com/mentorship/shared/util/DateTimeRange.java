package com.mentorship.shared.util;

import java.time.LocalDateTime;


public class DateTimeRange {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public DateTimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
}
