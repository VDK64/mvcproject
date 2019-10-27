package com.mvcproject.mvcproject.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomServerException.class)
    public ModelAndView handleServerErrors(CustomServerException serverExceptions) {
        ModelAndView model = serverExceptions.getModel();
        model.addObject("error", serverExceptions.getMessage());
        return model;
    }
}