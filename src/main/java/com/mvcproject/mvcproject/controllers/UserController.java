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
        User userFromDB = userService.getUserById(user.getId());
        model.addAttribute("friends", userService.getFriends(userFromDB));
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        return "friends";
    }

    @GetMapping("/friends/find_friends")
    public String getFindFriends(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        return "findFriends";
    }

    @PostMapping("/friends/find_friends")
    public ModelAndView findFriends(@AuthenticationPrincipal User user, ModelAndView model) {
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addObject("user", userFromDB);
        model.addObject("newMessages", userFromDB.isHaveNewMessages());
        model.addObject("newBets", userFromDB.isHaveNewBets());
        model.setViewName("findFriends");
        return model;
    }


    @GetMapping("/bets")
    public String bets(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        if (userFromDB.isHaveNewBets()) {
            betService.formModelForBets(model, userFromDB, "Opponent");
            return "bets";
        }
        betService.formModelForBets(model, userFromDB, "Owner");
        return "bets";
    }

    @PostMapping(value = "/bets", params = "chooseTable")
    public String getTable(@AuthenticationPrincipal User user, Model model,
                           @RequestParam String table) {
        betService.formModelForBets(model, userService.getUserById(user.getId()), table);
        return "bets";
    }

    @PostMapping(value = "/bets", params = "tablePage")
    public String betsTable(@AuthenticationPrincipal User user, Model model,
                            @RequestParam(required = false) String tableName,
                            @RequestParam(required = false) int page) {
        User userFromDB = userService.getUserById(user.getId());
        Page<Bet> response = betService.getBetInfo(userFromDB, tableName);
        int totalPages = response.getTotalPages();
        List<Bet> items = betService.listFromPage(response);
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        model.addAttribute("items", items);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("tableName", tableName);
        return "bets";
    }

    @GetMapping("/bets/createBet")
    public String createBet(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("friends", userService.getFriends(userFromDB));
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        return "createBet";
    }

    @PostMapping("/bets/createBet")
    public String createBet(@AuthenticationPrincipal User user, @RequestParam String game,
                            @RequestParam String gamemode, @RequestParam String value, @RequestParam String opponent,
                            @RequestParam String lobbyName, @RequestParam String password) {
        User userFromDB = userService.getUserById(user.getId());
        ModelAndView modelAndView = new ModelAndView("createBet");
        modelAndView.addObject("friends", userService.getFriends(userFromDB));
        modelAndView.addObject("newMessages", userFromDB.isHaveNewMessages());
        modelAndView.addObject("newBets", userFromDB.isHaveNewBets());
        UserService.ifAdmin(modelAndView, userFromDB);
        betService.createBetAndGame(userFromDB, game, gamemode, value, opponent, lobbyName, password,
                modelAndView);
        modelAndView.addObject("user", userFromDB);
        return "redirect:/bets";
    }

    @MessageMapping("/bet")
    public void betNotification(@AuthenticationPrincipal User user, @Payload BetDto betDto)
            throws JsonProcessingException {
        betService.messageParser(userService.getUserById(user.getId()), betDto);
    }

    @GetMapping("/bets/{id}")
    public String getDetails(@AuthenticationPrincipal User user, Model model, @PathVariable Long id) {
        User userFromDB = userService.getUserById(user.getId());
        Bet bet = betService.getBet(id);
        UserService.ifAdmin(model, userFromDB);
        betService.readNewBet(id, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("bet", bet);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        if (bet == null || betService.access(bet, userFromDB)) {
            return "errorPage";
        }
        return "details";
    }

    @PostMapping(value = "/bets/{id}", params = "confirmBet")
    private ModelAndView setConfirm(@AuthenticationPrincipal User user, ModelAndView modelAndView,
                                    @PathVariable Long id) {
        User userFromDB = userService.getUserById(user.getId());
        modelAndView.setViewName("details");
        UserService.ifAdmin(modelAndView, userFromDB);
        modelAndView.addObject("newMessages", userFromDB.isHaveNewMessages());
        modelAndView.addObject("newBets", userFromDB.isHaveNewBets());
        Bet bet = betService.setConfirm(id, userFromDB, modelAndView);
        modelAndView.addObject("bet", bet);
        betService.betInfo(new BetDto(bet.getId(), bet.getUser().getUsername(), bet.getOpponent().getUsername(),
                null, "showOtherInfo"));
        modelAndView.addObject("user", userFromDB);
        return modelAndView;
    }
}
