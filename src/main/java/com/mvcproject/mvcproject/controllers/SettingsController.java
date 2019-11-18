package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.SettingsService;
import com.mvcproject.mvcproject.services.UserService;
import com.mvcproject.mvcproject.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RequestMapping("/settings")
@Controller
public class SettingsController {
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private UserRepo userrepo;

    @GetMapping
    public String getSettings(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        UserService.ifAdmin(model, user);
        return "settings";
    }

    @PostMapping
    public String setAvatar(@AuthenticationPrincipal User user, Model model,
                            @RequestParam("file") MultipartFile file) throws IOException {
        settingsService.saveFile(file, user);
        model.addAttribute("user", user);
        UserService.ifAdmin(model, user);
        return "settings";
    }

    @PostMapping(params = "button2")
    public String setSettings(@AuthenticationPrincipal User user, Model model, @RequestParam String firstname,
                              @RequestParam String lastname, @RequestParam String username) {
        User updateUser = settingsService.setSettings(user, firstname, lastname, username, new ModelAndView("settings"));
        UserService.ifAdmin(model, updateUser);
        model.addAttribute("user", updateUser);
        model.addAttribute("ok", true);
        return "settings";
    }

    @PostMapping(params = "button")
    public ModelAndView deleteAvatar(@AuthenticationPrincipal User user, ModelAndView model) {
        UserService.ifAdmin(model, user);
        model.addObject("user", user);
        settingsService.deleteAvatar(user, model);
        return model;
    }
}
