package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.BetService;
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

import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private BetService betService;

    @RequestMapping("/")
    public String getMainPage(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        return "index";
    }

    @RequestMapping("/friend/{id}")
    public String getGuestPage(@AuthenticationPrincipal User user, Model model,
                               @PathVariable String id) {
        Map<String, Object> data = userService.isFriend(user, Long.parseLong(id));
        User userFromDB = (User) data.get("user");
        User friend = (User) data.get("friend");
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("friend", friend);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        model.addAttribute("isFriend", data.get("isFriend"));
        return "guestPage";
    }

    @PostMapping(value = "/friend/{id}", params = "sendMessageToFriend")
    public String sendMessage(@AuthenticationPrincipal User user, @RequestParam String friendId) {
        long dialogId = messageService.determineDialog(Long.parseLong(friendId), user);
        return "redirect:/messages/" + dialogId;
    }

    @RequestMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
                           Model model) {
        if (error != null && error.equals("disabled"))
            model.addAttribute("error", "disabled");
        else if (error != null)
            model.addAttribute("error", "credentials");
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String firstname,
                               @RequestParam String lastname,
                               @RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String email,
                               RedirectAttributes attributes) {
        userService.createUser(firstname, lastname, username, password, email,
                new ModelAndView("register"), null);
        attributes.addFlashAttribute("ok", "true");
        return "redirect:/login";
    }

    @GetMapping("email/activate/{code}")
    public String emailActivate(@PathVariable String code, Model model,
                                @Value("${success.confirm}") String ok,
                                @Value("${wrong.confirm}") String wrongConfirm) {
        boolean res = userService.confirmEmail(code, model);
        if (res)
            model.addAttribute("msg", ok);
        else
            model.addAttribute("msg", wrongConfirm);
        return "emailConfirm";
    }
}