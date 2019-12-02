package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.BetService;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    BetService betService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/userList")
    public String getList(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("newBets", betService.haveNewBets(user));
        Iterable<User> allUsers = userService.getAllUsers();
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("users", allUsers);
        model.addAttribute("username", user.getUsername());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{user}")
    public String getEditUser(@PathVariable User user, Model model) {
        UserService.ifAdmin(model, user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("newBets", betService.haveNewBets(user));
        model.addAttribute("user", user);
        model.addAttribute("authorities", Role.values());
        model.addAttribute("username", user.getUsername());
        return "editUser";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{user}")
    public String editUser(@PathVariable User user, @RequestParam String firstname,
                           @RequestParam String lastname, @RequestParam String username, @RequestParam String password,
                           @RequestParam Map<String, String> authorities, Model model) {
        userService.changeUser(user, firstname, lastname, username, password, authorities, new ModelAndView("editUser"));
        Iterable<User> allUsers = userService.getAllUsers();
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("newBets", betService.haveNewBets(user));
        model.addAttribute("users", allUsers);
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        UserService.ifAdmin(model, user);
        return "userList";
    }
}
