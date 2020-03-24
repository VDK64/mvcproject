package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.dto.DialogDtoResponse;
import com.mvcproject.mvcproject.dto.MessageDto;
import com.mvcproject.mvcproject.entities.User;
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
        model.addAttribute("newMessages", messageService.haveNewMessages(userFromDB));
        model.addAttribute("newBets", betService.haveNewBets(userFromDB));
        Set<DialogDtoResponse> response = messageService.getDialogs(userFromDB.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        if (!response.isEmpty())
            model.addAttribute("dialogs", response);
        return "dialogs";
    }

    @RequestMapping("/messages/{dialogId}")
    public String getMessages(@AuthenticationPrincipal User user,
                              @PathVariable Long dialogId, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        if (!messageService.accessRouter(userFromDB.getId(), dialogId)) { return "errorPage"; }
        messageService.readNewMessage(dialogId);
        List<MessageDto> response = messageService.loadMessages(dialogId);
        model.addAttribute("newBets", betService.haveNewBets(userFromDB));
        model.addAttribute("interlocutor", messageService.getInterlocutor(dialogId, userFromDB.getId()));
        model.addAttribute("messages", response);
        model.addAttribute("dialogId", dialogId);
        return "messages";
    }

    @MessageMapping("/room")
    public void sendSpecific(@Payload MessageDto msg) {
        MessageDto out = messageService.sendMessage(msg);
        template.convertAndSendToUser(msg.getTo(), "/queue/updates", out);
    }

    @MessageMapping("/newMessage")
    public void updateMessage(@Payload MessageDto msg) {
        messageService.readNewMessage(msg.getDialogId());
    }
}