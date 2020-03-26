package com.mvcproject.mvcproject.exceptions;

import com.mvcproject.mvcproject.dto.BetDto;
import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.BetService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class CustomExceptionHandler {
    @Autowired
    private BetService betService;
    @Autowired
    private SimpMessagingTemplate template;

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
//        if (user.getAuthorities().contains(Role.valueOf("ADMIN"))) {
//            model.addObject("admin", true);
//        }
        UserService.ifAdmin(model, user);
        model.addObject("error", ServerErrors.FILE_LIMIT);
        return model;
    }

    @ExceptionHandler(ErrorPageException.class)
    public ModelAndView errorPage(ErrorPageException ex) {
        return ex.getModel();
    }

    @MessageExceptionHandler
    @SendToUser(destinations="/queue/events")
    public BetDto handleException(InternalServerExceptions exception) {
        BetDto betDto = new BetDto(exception.getUser(), exception.getOpponent(), null, exception.getMessage());
        template.convertAndSendToUser(betService.detectDestinationNotPrincipal(exception.getPrincipal(), betDto),
                "/queue/events", betDto);
        return betDto;
    }
}