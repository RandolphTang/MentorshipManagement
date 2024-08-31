package com.mentorship.shared.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDto {
    private Long feedbackId;
    private Long mentorshipId;
    private Long mentorId;
    private String content;
}
