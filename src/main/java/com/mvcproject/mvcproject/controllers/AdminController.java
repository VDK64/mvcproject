package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String editUser(@PathVariable User user) {
        return "redirect:/admin/userList";
    }

}
