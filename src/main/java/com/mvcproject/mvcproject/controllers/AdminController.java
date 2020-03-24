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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private BetService betService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/userList")
    public String getList(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        model.addAttribute("newMessages", messageService.haveNewMessages(userFromDB));
        model.addAttribute("newBets", betService.haveNewBets(userFromDB));
        Iterable<User> allUsers = userService.getAllUsers();
        UserService.ifAdmin(model, user);
        model.addAttribute("user", userFromDB);
        model.addAttribute("users", allUsers);
        model.addAttribute("username", userFromDB.getUsername());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{user}")
    public String getEditUser(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("newMessages", messageService.haveNewMessages(userFromDB));
        model.addAttribute("newBets", betService.haveNewBets(userFromDB));
        model.addAttribute("user", userFromDB);
        model.addAttribute("authorities", Role.values());
        model.addAttribute("username", userFromDB.getUsername());
        return "editUser";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{user}")
    public String editUser(@AuthenticationPrincipal User user, @RequestParam String firstname,
                           @RequestParam String lastname, @RequestParam String username, @RequestParam String password,
                           @RequestParam Map<String, String> authorities, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        userService.changeUser(userFromDB, firstname, lastname, username, password, authorities,
                new ModelAndView("editUser"));
        Iterable<User> allUsers = userService.getAllUsers();
        model.addAttribute("newMessages", messageService.haveNewMessages(userFromDB));
        model.addAttribute("newBets", betService.haveNewBets(userFromDB));
        model.addAttribute("users", allUsers);
        model.addAttribute("user", userFromDB);
        model.addAttribute("username", userFromDB.getUsername());
        UserService.ifAdmin(model, userFromDB);
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/userList", params = "getToken")
    public String getToken(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        model.addAttribute("newMessages", messageService.haveNewMessages(userFromDB));
        model.addAttribute("newBets", betService.haveNewBets(userFromDB));
        Iterable<User> allUsers = userService.getAllUsers();
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("token", Dota2Controller.token);
        model.addAttribute("users", allUsers);
        model.addAttribute("username", userFromDB.getUsername());
        return "userList";
    }
}