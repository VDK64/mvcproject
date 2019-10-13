package com.mvcproject.mvcproject.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomServerException extends RuntimeException {
    private String source;
    public CustomServerException(String message, String source) {
        super(message);
        this.source = source;
    }
}
