package com.donatus.simpletaskmanager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GenericException extends RuntimeException{
    private final HttpStatus statusCode;
    private final String debugMessage;
    public GenericException(HttpStatus statusCode, String message, String debugMessage) {
        super(message);
        this.statusCode = statusCode;
        this.debugMessage = debugMessage;
    }
}
