package com.mvcproject.mvcproject.exceptions;

import lombok.Getter;
import org.springframework.web.servlet.ModelAndView;

@Getter
public class ErrorPageException extends RuntimeException {
    private final ModelAndView model;

    public ErrorPageException(ModelAndView model) {
        this.model = model;
    }
}
