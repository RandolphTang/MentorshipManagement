package com.mentorship.shared.dto;

import com.mentorship.shared.Enums.MentorshipStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorshipDto {
    private Long mentorshipId;
    private Long mentorId;
    private Long menteeId;
    private MentorshipStatus status;
}
