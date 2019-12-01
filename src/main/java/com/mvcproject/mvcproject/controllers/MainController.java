package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @RequestMapping("/")
    public String getMainPage(@AuthenticationPrincipal User user, Model model) {
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
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
    public String registerUser(@RequestParam String firstname,
                               @RequestParam String lastname,
                               @RequestParam String username, @RequestParam String password, @RequestParam String email,
                               RedirectAttributes attributes) {
        userService.createUser(firstname, lastname, username, password, email, new ModelAndView("register"));
        attributes.addFlashAttribute("ok", "true");
        return "redirect:/login";
    }

    @GetMapping("email/activate/{code}")
    public String emailActivate(@PathVariable String code, Model model, @Value("${success.confirm}") String ok,
                                @Value("${wrong.confirm}") String wrongConfirm) {
        boolean res = userService.confirmEmail(code, model);
        if (res) { model.addAttribute("msg", ok); } else { model.addAttribute("msg", wrongConfirm); }
        model.addAttribute("admin", false);
        model.addAttribute("user", new User());
        return "emailConfirm";
    }

}