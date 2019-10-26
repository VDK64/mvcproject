package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.UserService;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/userList")
    public String getList(Model model) {
        Iterable<User> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "userList";
    }

    @GetMapping("/{user}")
    public String getEditUser(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("authorities", Role.values());
        return "editUser";
    }

    @PostMapping("/{user}")
    public String editUser(@PathVariable User user, @RequestParam String firstname,
                           @RequestParam String lastname, @RequestParam String username, @RequestParam String password,
                           @RequestParam Map<String, String> authorities, Model model) {
        userService.changeUser(user, firstname, lastname, username, password, authorities);
        Iterable<User> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        return "userList";
    }
}
