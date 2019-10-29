package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Stream;

@RequestMapping("/dialogs")
@Controller
public class MessageController {
    @Autowired
    MessageService messageService;

    @GetMapping()
    public String getDialogs(@AuthenticationPrincipal User user) {
        messageService.getDialogs(user);
        return "dialogs";
    }


}
