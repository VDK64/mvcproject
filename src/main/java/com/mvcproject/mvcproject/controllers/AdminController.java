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
    private BetService betService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/userList")
    public String getList(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        Iterable<User> allUsers = userService.getAllUsers();
        UserService.ifAdmin(model, user);
        model.addAttribute("user", userFromDB);
        model.addAttribute("users", allUsers);
        model.addAttribute("username", userFromDB.getUsername());
        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public String getEditUser(@AuthenticationPrincipal User principal, @PathVariable Long id, Model model) {
        User userFromDB = userService.getUserById(principal.getId());
        User find = userService.getUserById(id);
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        model.addAttribute("find", find);
        model.addAttribute("user", userFromDB);
        model.addAttribute("authorities", Role.values());
        model.addAttribute("username", userFromDB.getUsername());
        return "editUser";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}")
    public String editUser(@AuthenticationPrincipal User user, @PathVariable Long id,
                           @RequestParam Map<String, String> params, Model model) {
        ModelAndView error = new ModelAndView("editUser");
        User userFromDB = userService.getUserById(user.getId());
        User find = userService.getUserById(id);
        userService.changeUser(find, params,
                error, userFromDB);
        Iterable<User> allUsers = userService.getAllUsers();
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
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
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        Iterable<User> allUsers = userService.getAllUsers();
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("token", Dota2Controller.token);
        model.addAttribute("users", allUsers);
        model.addAttribute("username", userFromDB.getUsername());
        return "userList";
    }
}