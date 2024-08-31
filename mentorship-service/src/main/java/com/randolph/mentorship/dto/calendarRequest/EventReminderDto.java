package com.randolph.mentorship.dto.calendarRequest;

import lombok.Data;

@Data
public class EventReminderDto {
    private String method;
    private Integer minutes;
}
