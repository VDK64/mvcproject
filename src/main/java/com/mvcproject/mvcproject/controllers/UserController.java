package com.mvcproject.mvcproject.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mvcproject.mvcproject.dto.BetDto;
import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.BetService;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
        model.addAttribute("newBets", betService.haveNewBets(user));
        return "friends";
    }

    @GetMapping("/bets")
    public String bets(@AuthenticationPrincipal User user, Model model) {
        boolean haveNewBets = betService.haveNewBets(user);
        if (haveNewBets) {
            betService.formModelForBets(model, user, "Opponent");
            return "bets";
        }
        betService.formModelForBets(model, user, "Owner");
        return "bets";
    }

    @PostMapping(value = "/bets", params = "chooseTable")
    public String getTable(@AuthenticationPrincipal User user, Model model,
                           @RequestParam String table) {
        betService.formModelForBets(model, user, table);
        return "bets";
    }

    @PostMapping(value = "/bets", params = "tablePage")
    public String betsTable(@AuthenticationPrincipal User user, Model model,
                            @RequestParam(required = false) String tableName,
                            @RequestParam(required = false) int page) {
        Page<Bet> response = betService.getBetInfo(user, tableName);
        int totalPages = response.getTotalPages();
        List<Bet> items = betService.listFromPage(response);
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("newBets", betService.haveNewBets(user));
        model.addAttribute("items", items);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("tableName", tableName);
        return "bets";
    }

    @GetMapping("/bets/createBet")
    public String createBet(@AuthenticationPrincipal User user, Model model) {
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("friends", userService.getFriends(user));
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("newBets", betService.haveNewBets(user));
        return "createBet";
    }

    @PostMapping("/bets/createBet")
    public String createBet(@AuthenticationPrincipal User user, @RequestParam String game,
                            @RequestParam String gamemode, @RequestParam String value, @RequestParam String opponent,
                            @RequestParam String lobbyName, @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView("createBet");
        modelAndView.addObject("friends", userService.getFriends(user));
        modelAndView.addObject("user", user);
        modelAndView.addObject("newMessages", messageService.haveNewMessages(user));
        modelAndView.addObject("newBets", betService.haveNewBets(user));
        UserService.ifAdmin(modelAndView, user);
        betService.createBetAndGame(user, game, gamemode, value, opponent, lobbyName, password,
                modelAndView);
        return "redirect:/bets";
    }

    @MessageMapping("/bet")
    public void betNotification(@AuthenticationPrincipal User user, @Payload BetDto betDto)
            throws JsonProcessingException {
        betService.messageParser(user, betDto);
    }

    @GetMapping("/bets/{id}")
    public String getDetails(@AuthenticationPrincipal User user, Model model, @PathVariable Long id) {
        Bet bet = betService.getBet(id);
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("bet", bet);
        model.addAttribute("newMessages", messageService.haveNewMessages(user));
        model.addAttribute("newBets", betService.haveNewBets(user));
        if (bet == null || betService.access(bet, user)) {
            return "errorPage";
        }
        betService.readNewBet(id);
        return "details";
    }

    @PostMapping(value = "/bets/{id}", params = "confirmBet")
    private ModelAndView setConfirm(@AuthenticationPrincipal User user, ModelAndView modelAndView,
                                    @PathVariable Long id) {
        modelAndView.setViewName("details");
        UserService.ifAdmin(modelAndView, user);
        modelAndView.addObject("newMessages", messageService.haveNewMessages(user));
        modelAndView.addObject("newBets", betService.haveNewBets(user));
        modelAndView.addObject("user", user);
        Bet bet = betService.setConfirm(id, user, modelAndView);
        modelAndView.addObject("bet", bet);
        betService.betInfo(new BetDto(bet.getId(), bet.getUser().getUsername(), bet.getOpponent().getUsername(),
                null, "showOtherInfo"));
        return modelAndView;
    }
}
