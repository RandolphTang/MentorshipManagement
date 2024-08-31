package com.randolph.mentorship.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventRequestDto {

    private Long mentorId;

    private List<Long> menteeIds;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private String title;
    private String location;
    private String description;
}
