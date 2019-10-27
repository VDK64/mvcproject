package com.mvcproject.mvcproject.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.servlet.ModelAndView;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomServerException extends RuntimeException {
    private ModelAndView model;
    public CustomServerException(String message, ModelAndView model) {
        super(message);
        this.model = model;
    }
}
