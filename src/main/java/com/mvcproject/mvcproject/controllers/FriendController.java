package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/friends")
public class FriendController {
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @GetMapping
    public String friends(@AuthenticationPrincipal User user, Model model) {
        Map<String, Object> data = userService.getFriendsSeparately(user.getId());
        User userFromDB = (User) data.get("user");
        model.addAttribute("friends", data.get("friends"));
        model.addAttribute("unconfirmed", data.get("unconfirmed"));
        model.addAttribute("invites", data.get("invites"));
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        return "friends";
    }

    @PostMapping(params = "confirmInvite")
    public String addFriend(@AuthenticationPrincipal User user,
                            @RequestParam String inviteUsername, Model model) {
        User userFromDB = userService.addFriend(user.getId(), inviteUsername);
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        return "redirect:/friends";
    }

    @PostMapping(params = "sendMessageToFriend")
    public String sendMessage(@AuthenticationPrincipal User user,
                              @RequestParam String friendId) {
        long dialogId = messageService.determineDialog(Long.parseLong(friendId), user);
        return "redirect:/messages/" + dialogId;
    }

    @GetMapping("/find_friends")
    public String getFindFriends(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        return "findFriends";
    }

    @PostMapping("/find_friends")
    public ModelAndView findFriends(@AuthenticationPrincipal User user, @RequestParam String username,
                                    ModelAndView model) {
        model.setViewName("findFriends");
        Map<String, Object> data = userService.isFriend(user, username);
        User userFromDB = (User) data.get("user");
        User findUser = (User) data.get("friend");
        UserService.ifAdmin(model, userFromDB);
        model.addObject("user", userFromDB);
        model.addObject("newMessages", userFromDB.isHaveNewMessages());
        model.addObject("newBets", userFromDB.isHaveNewBets());
        model.addObject("findUser", findUser);
        if (userFromDB.equals(findUser))
            model.addObject("isFriend", true);
        else
            model.addObject("isFriend", data.get("isFriend"));
        return model;
    }

    @PostMapping(value = "/find_friends", params = "addFriend")
    public ModelAndView addFriend(@AuthenticationPrincipal User user, @RequestParam String username,
                                  ModelAndView model) {
        model.setViewName("findFriends");
        User userFromDB = userService.addFriend(user.getId(), username);
        UserService.ifAdmin(model, userFromDB);
        model.addObject("user", userFromDB);
        model.addObject("newMessages", userFromDB.isHaveNewMessages());
        model.addObject("newBets", userFromDB.isHaveNewBets());
        return model;
    }
}
