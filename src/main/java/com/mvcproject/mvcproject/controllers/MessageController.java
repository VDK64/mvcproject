package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.dto.DialogDtoResponse;
import com.mvcproject.mvcproject.dto.MessageDto;
import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.ErrorPageException;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.BetService;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private UserService userService;
    @Autowired
    private BetService betService;
    @Autowired
    private UserRepo userRepo;

    @RequestMapping("/dialogs")
    public String getDialogs(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        Set<DialogDtoResponse> response = messageService.getDialogs(userFromDB.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        if (!response.isEmpty())
            model.addAttribute("dialogs", response);
        return "dialogs";
    }

    @RequestMapping("/messages/{dialogId}")
    public ModelAndView getMessages(@AuthenticationPrincipal User user,
                                    @PathVariable Long dialogId, ModelAndView model) {
        List<MessageDto> messageList = new ArrayList<>();
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addObject("user", userFromDB);
        model.addObject("newBets", userFromDB.isHaveNewBets());
        Dialog dialog = messageService.accessRouter(messageList, userFromDB, dialogId, model);
        messageService.readNewMessage(userFromDB, dialog);
        model.addObject("interlocutor", messageService.getInterlocutor(dialog, userFromDB.getId()));
        model.addObject("messages", messageList);
        model.addObject("dialogId", dialogId);
        model.setViewName("messages");
        return model;
    }

    @MessageMapping("/room")
    public void sendSpecific(@Payload MessageDto msg) {
        MessageDto out = messageService.sendMessage(msg);
        template.convertAndSendToUser(msg.getTo(), "/queue/updates", out);
    }

    @MessageMapping("/newMessage")
    public void updateMessage(@AuthenticationPrincipal User user, @Payload MessageDto msg) {
        messageService.readNewMessage(user, msg.getDialogId());
    }
}