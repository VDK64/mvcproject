package com.mvcproject.mvcproject.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class InternalServerExceptions extends RuntimeException {
    private String user;
    private String opponent;
    private String principal;

    public InternalServerExceptions(String message) {
        super(message);
    }

    public InternalServerExceptions(String message, String user, String opponent, String principal) {
        super(message);
        this.user = user;
        this.opponent = opponent;
        this.principal = principal;
    }
}
