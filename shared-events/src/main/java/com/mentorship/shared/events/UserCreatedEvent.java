package com.mentorship.shared.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mentorship.shared.Enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreatedEvent {

    private final Long userId;
    private final String email;
    private final String username;

    @JsonCreator
    public UserCreatedEvent(
            @JsonProperty("userId") Long userId,
            @JsonProperty("email") String email,
            @JsonProperty("username") String username) {
        this.userId = userId;
        this.email = email;
        this.username = username;
    }

}
