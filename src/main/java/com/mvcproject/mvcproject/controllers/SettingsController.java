package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.config.JOpenId;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.BetService;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.SettingsService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequestMapping("/settings")
@Controller
public class SettingsController {
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    private BetService betService;

    @GetMapping
    public String getSettings(@AuthenticationPrincipal User user, Model model, HttpServletRequest request) {
        String identity = request.getParameter("openid.identity");
        if (identity != null && user.getSteamId() == null) {
            settingsService.setSteamId(user, identity);
        }
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("newBets", betService.haveNewBets(user));
        model.addAttribute("user", userService.getUserById(user.getId()));
        model.addAttribute("auth", JOpenId.getUrl());
        UserService.ifAdmin(model, user);
        return "settings";
    }

    @PostMapping
    public String setAvatar(@AuthenticationPrincipal User user, Model model,
                            @RequestParam("file") MultipartFile file) throws IOException {
        settingsService.saveFile(file, user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("newBets", betService.haveNewBets(user));
        model.addAttribute("user", userService.getUserById(user.getId()));
        model.addAttribute("auth", JOpenId.getUrl());
        UserService.ifAdmin(model, user);
        return "settings";
    }

    @PostMapping(params = "changeData")
    public String setSettings(@AuthenticationPrincipal User user, Model model, @RequestParam String firstname,
                              @RequestParam String lastname, @RequestParam String username) {
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("newBets", betService.haveNewBets(user));
        User updateUser = settingsService.setSettings(user, firstname, lastname, username, new ModelAndView("settings"));
        UserService.ifAdmin(model, updateUser);
        model.addAttribute("user", updateUser);
        model.addAttribute("ok", "Please, relogin to update changes!");
        model.addAttribute("auth", JOpenId.getUrl());
        return "settings";
    }

    @PostMapping(params = "button")
    public ModelAndView deleteAvatar(@AuthenticationPrincipal User user, ModelAndView model) {
        model.addObject("newMessages", messageService.haveNewMessages(user));
        model.addObject("newBets", betService.haveNewBets(user));
        UserService.ifAdmin(model, user);
        model.addObject("user", userService.getUserById(user.getId()));
        model.addObject("auth", JOpenId.getUrl());
        settingsService.deleteAvatar(user, model);
        return model;
    }

    @PostMapping(params = "deposit")
    public ModelAndView deposit(@AuthenticationPrincipal User user, ModelAndView model,
                                @RequestParam String value) {
        UserService.ifAdmin(model, user);
        User userFromDB = userService.getUserById(user.getId());
        model.addObject("newMessages", messageService.haveNewMessages(user));
        model.addObject("newBets", betService.haveNewBets(user));
        model.addObject("auth", JOpenId.getUrl());
        settingsService.deposit(userFromDB, value, model);
        model.addObject("user", userFromDB);
        model.addObject("ok", "Your deposit was successfully replenished!");
        return model;
    }

    @PostMapping(params = "withdraw")
    public ModelAndView withdraw(@AuthenticationPrincipal User user, ModelAndView model,
                                 @RequestParam String value) {
        UserService.ifAdmin(model, user);
        User userFromDB = userService.getUserById(user.getId());
        model.addObject("newMessages", messageService.haveNewMessages(user));
        model.addObject("newBets", betService.haveNewBets(user));
        model.addObject("auth", JOpenId.getUrl());
        settingsService.withdraw(userFromDB, value, model);
        model.addObject("user", userFromDB);
        model.addObject("ok", "Withdraw was successfully!");
        return model;
    }
}
