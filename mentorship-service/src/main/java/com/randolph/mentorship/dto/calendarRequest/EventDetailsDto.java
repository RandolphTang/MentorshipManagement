package com.randolph.mentorship.dto.calendarRequest;

import lombok.Data;

@Data
public class EventDetailsDto {
    private String summary;
    private String description;
    private EventDateTimeDto start;
    private EventDateTimeDto end;
    private String location;
    private EventReminderDto reminder;
    private EventRecurrenceDto recurrence;
    private Long mentorshipRelationshipId;
}
