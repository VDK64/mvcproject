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
    private UserService userService;
    @Autowired
    private BetService betService;

    @GetMapping
    public String getSettings(@AuthenticationPrincipal User user, Model model, HttpServletRequest request) {
        User userFromDB = userService.getUserById(user.getId());
        String identity = request.getParameter("openid.identity");
        if (identity != null && userFromDB.getSteamId() == null) {
            settingsService.setSteamId(userFromDB, identity);
        }
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        model.addAttribute("user", userFromDB);
        model.addAttribute("auth", JOpenId.getUrl());
        UserService.ifAdmin(model, userFromDB);
        return "settings";
    }

    @PostMapping
    public String setAvatar(@AuthenticationPrincipal User user, Model model,
                            @RequestParam("file") MultipartFile file) throws IOException {
        User userFromDB = userService.getUserById(user.getId());
        settingsService.saveFile(file, userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        model.addAttribute("user", userFromDB);
        model.addAttribute("auth", JOpenId.getUrl());
        UserService.ifAdmin(model, userFromDB);
        return "settings";
    }

    @PostMapping(params = "changeData")
    public String setSettings(@AuthenticationPrincipal User user, Model model, @RequestParam String firstname,
                              @RequestParam String lastname, @RequestParam String username) {
        User userFromDB = userService.getUserById(user.getId());
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        User updateUser = settingsService.setSettings(userFromDB, firstname, lastname, username,
                new ModelAndView("settings"));
        UserService.ifAdmin(model, updateUser);
        model.addAttribute("user", updateUser);
        model.addAttribute("ok", "Please, relogin to update changes!");
        model.addAttribute("auth", JOpenId.getUrl());
        return "settings";
    }

    @PostMapping(params = "button")
    public ModelAndView deleteAvatar(@AuthenticationPrincipal User user, ModelAndView model) {
        User userFromDB = userService.getUserById(user.getId());
        model.addObject("newMessages", userFromDB.isHaveNewMessages());
        model.addObject("newBets", userFromDB.isHaveNewBets());
        UserService.ifAdmin(model, userFromDB);
        model.addObject("auth", JOpenId.getUrl());
        settingsService.deleteAvatar(userFromDB, model);
        model.addObject("user", userFromDB);
        return model;
    }

    @PostMapping(params = "deposit")
    public ModelAndView deposit(@AuthenticationPrincipal User user, ModelAndView model,
                                @RequestParam String value) {
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addObject("newMessages", userFromDB.isHaveNewMessages());
        model.addObject("newBets", userFromDB.isHaveNewBets());
        model.addObject("auth", JOpenId.getUrl());
        settingsService.deposit(userFromDB, value, model);
        model.addObject("user", userFromDB);
        model.addObject("ok", "Your deposit was successfully replenished!");
        return model;
    }

    @PostMapping(params = "withdraw")
    public ModelAndView withdraw(@AuthenticationPrincipal User user, ModelAndView model,
                                 @RequestParam String value) {
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addObject("newMessages", userFromDB.isHaveNewMessages());
        model.addObject("newBets", userFromDB.isHaveNewBets());
        model.addObject("auth", JOpenId.getUrl());
        settingsService.withdraw(userFromDB, value, model);
        model.addObject("user", userFromDB);
        model.addObject("ok", "Withdraw was successfully!");
        return model;
    }
}
