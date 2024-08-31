package com.randolph.mentorship.dto.calendarRequest;

import lombok.Data;

@Data
public class EventRecurrenceDto {

    private String frequency;
    private Integer interval;
    private Integer count;
    private String until;
}
