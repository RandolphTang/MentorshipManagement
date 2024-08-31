package com.randolph.mentorship.dto.calendarRequest;

import com.google.api.services.calendar.model.Event;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequestDto {
    private Long mentorId;
    private List<Long> menteeIds;
    private EventDetailsDto eventDetails;
}
