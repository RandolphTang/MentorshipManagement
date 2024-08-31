package com.randolph.mentorship.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RequestExistedException extends RuntimeException {

    public RequestExistedException(String message){
        super(message);
    }
}
