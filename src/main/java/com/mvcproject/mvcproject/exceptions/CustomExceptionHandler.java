package com.mvcproject.mvcproject.exceptions;

import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomServerException.class)
    public ModelAndView handleServerErrors(CustomServerException serverExceptions) {
        ModelAndView model = serverExceptions.getModel();
        model.addObject("error", serverExceptions.getMessage());
        return model;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxUploadException(@AuthenticationPrincipal User user,
                                                 MaxUploadSizeExceededException ex) {
        ModelAndView model = new ModelAndView("/settings");
        model.addObject("user", user);
        if (user.getAuthorities().contains(Role.valueOf("ADMIN"))) {
            model.addObject("admin", true);
        }
        model.addObject("error", ServerErrors.FILE_LIMIT);
        return model;
    }
}