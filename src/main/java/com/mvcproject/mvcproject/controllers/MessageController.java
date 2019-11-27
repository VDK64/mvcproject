package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.dto.DialogDtoResponse;
import com.mvcproject.mvcproject.dto.MessageDto;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private SimpMessagingTemplate template;

    @RequestMapping("/dialogs")
    public String getDialogs(@AuthenticationPrincipal User user, Model model) {
        Set<DialogDtoResponse> response = messageService.getDialogs(user.getId());
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        if (!response.isEmpty())
            model.addAttribute("dialogs", response);
        return "dialogs";
    }

    @RequestMapping("/messages/{dialogId}")
    public String getMessages(@AuthenticationPrincipal User user,
                              @PathVariable Long dialogId, Model model) {
        List<MessageDto> response = messageService.loadMessages(user, dialogId);
        UserService.ifAdmin(model, user);
        model.addAttribute("interlocutor", messageService.getInterlocutor(dialogId, user.getId()));
        model.addAttribute("user", user);
        model.addAttribute("messages", response);
        model.addAttribute("dialogId", dialogId);
        return "messages";
    }

    @MessageMapping("/room")
    public void sendSpecific(@Payload MessageDto msg, Principal user) {
        messageService.sendMessage(user, msg);
    }
}
