package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

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
    public String setSettings(@AuthenticationPrincipal User user, Model model,
                              @RequestParam("file") MultipartFile file) throws IOException {
        settingsService.saveFile(file, user);
        model.addAttribute("user", user);
        return "settings";
    }
}
