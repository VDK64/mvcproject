package com.mvcproject.mvcproject.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InternalServerExceptions extends RuntimeException{
    public InternalServerExceptions(String message) {
        super(message);
    }
}
