package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.BetService;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private BetService betService;

    @GetMapping("/friends")
    public String friends(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("friends", userService.getFriends(user));
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        return "friends";
    }

    @GetMapping("/bets")
    public String bets(@AuthenticationPrincipal User user, Model model) {
        Page<Bet> byUser = betService.getBetInfo(user, "user");
        Page<Bet> byOpponent = betService.getBetInfo(user, "opponent");
        int totalPagesUser = byUser.getTotalPages();
        int totalPagesOpponent = byOpponent.getTotalPages();
        List<Bet> betsUser = betService.listFromPage(byUser);
        List<Bet> betsOpponent = betService.listFromPage(byOpponent);
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("owners", betsUser);
        model.addAttribute("opponents", betsOpponent);
        model.addAttribute("totalUser", totalPagesUser);
        model.addAttribute("totalOpponent", totalPagesOpponent);
        return "bets";
    }

    @PostMapping("/bets")
    public String betsTable(@AuthenticationPrincipal User user, Model model,
                            @RequestParam(required = false) String tableOwner,
                            @RequestParam(required = false) String tableOpponent,
                            @RequestParam(required = false) List<Bet> owners) {
        if (tableOwner != null) {
            Page<Bet> byUser = betService.getBetInfo(user, "user");
            int totalPagesUser = byUser.getTotalPages();
            List<Bet> betsUser = betService.listFromPage(byUser);
            model.addAttribute("owners", betsUser);
            model.addAttribute("totalUser", totalPagesUser);
        }
        if (tableOpponent != null) {
            Page<Bet> byOpponent = betService.getBetInfo(user, "opponent");
            int totalPagesOpponent = byOpponent.getTotalPages();
            List<Bet> betsOpponent = betService.listFromPage(byOpponent);
            model.addAttribute("opponents", betsOpponent);
            model.addAttribute("totalOpponent", totalPagesOpponent);
        }
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        return "bets";
    }

    @GetMapping("/bets/createBet")
    public String createBet(@AuthenticationPrincipal User user, Model model) {
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("friends", userService.getFriends(user));
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        return "createBet";
    }

    @PostMapping("/bets/createBet")
    public String createBet(@AuthenticationPrincipal User user, Model model, @RequestParam String game,
                            @RequestParam String gamemode, @RequestParam String value, @RequestParam String opponent,
                            @RequestParam String lobbyName, @RequestParam String password) {
        Bet betAndGame = betService.createBetAndGame(user, game, gamemode, value, opponent, lobbyName, password);
        UserService.ifAdmin(model, user);
        model.addAttribute("friends", userService.getFriends(user));
        model.addAttribute("user", user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        return "createBet";
    }
}
