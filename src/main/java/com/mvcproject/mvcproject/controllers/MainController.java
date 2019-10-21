package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {
    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String getMainPage(@AuthenticationPrincipal User user, Model model) {
        if (user.getAuthorities().contains(Role.ADMIN))
            model.addAttribute("admin", true);
        return "index";
    }

    @RequestMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout, Model model) {
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String firstname, @RequestParam String lastname,
                               @RequestParam String username, @RequestParam String password, RedirectAttributes attributes) {
        userService.createUser(firstname, lastname, username, password, "register");
        attributes.addFlashAttribute("ok", "true");
        return "redirect:/login";
    }

    @GetMapping("/userlist")
    public String getList() {
        return "userlist";
    }
}