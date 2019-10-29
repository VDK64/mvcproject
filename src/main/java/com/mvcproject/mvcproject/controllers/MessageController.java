package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.dto.UserDtoResponse;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.StringReader;
import java.util.Set;
import java.util.stream.Stream;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @RequestMapping("/dialogs")
    public String getDialogs(@AuthenticationPrincipal User user, Model model) {
        Set<UserDtoResponse> response = messageService.getDialogs(user.getId());
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        if (response.size() > 0)
            model.addAttribute("dialogs", response);
        return "dialogs";
    }

    @RequestMapping("/messages")
    public String getMessages(@AuthenticationPrincipal User user, Model model) {
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        return "messages";
    }

}
