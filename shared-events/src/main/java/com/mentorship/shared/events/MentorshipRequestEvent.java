package com.mentorship.shared.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mentorship.shared.Enums.MentorshipStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorshipRequestEvent {

    private final Long requestId;
    private final Long menteeId;
    private final Long mentorId;
    private final MentorshipStatus status;

    @JsonCreator
    public MentorshipRequestEvent(
            @JsonProperty("requestId") Long requestId,
            @JsonProperty("menteeId") Long menteeId,
            @JsonProperty("mentorId") Long mentorId,
            @JsonProperty("status") MentorshipStatus status) {
        this.requestId = requestId;
        this.menteeId = menteeId;
        this.mentorId = mentorId;
        this.status = status;
    }
}
