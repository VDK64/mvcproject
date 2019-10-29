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

import java.util.Set;
import java.util.stream.Stream;

@RequestMapping("/dialogs")
@Controller
public class MessageController {
    @Autowired
    MessageService messageService;

    @GetMapping()
    public String getDialogs(@AuthenticationPrincipal User user, Model model) {
        Set<UserDtoResponse> response = messageService.getDialogs(user.getId());
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        if (response.size() > 0)
            model.addAttribute("dialogs", response);
        return "dialogs";
    }


}
