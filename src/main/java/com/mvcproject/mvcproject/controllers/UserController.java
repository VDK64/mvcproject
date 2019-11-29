package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @GetMapping("/friends")
    public String friends(@AuthenticationPrincipal User user, Model model) {
        List<User> friends = userService.getFriends(user);
        model.addAttribute("friends", friends);
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        return "friends";
    }

    @GetMapping("/bets")
    public String bets(@AuthenticationPrincipal User user, Model model) {
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        return "bets";
    }
}
