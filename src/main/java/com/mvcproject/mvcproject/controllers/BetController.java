package com.mvcproject.mvcproject.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mvcproject.mvcproject.dto.BetDto;
import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.services.BetService;
import com.mvcproject.mvcproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bets")
public class BetController {
    @Autowired
    private BetService betService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String bets(@AuthenticationPrincipal User user, Model model) {
        User userFromDB = userService.getUserById(user.getId());
        if (userFromDB.isHaveNewBets()) {
            betService.formModelForBets(model, userFromDB, "Opponent");
            return "bets";
        }
        betService.formModelForBets(model, userFromDB, "Owner");
        return "bets";
    }

    @PostMapping(params = "chooseTable")
    public String getTable(@AuthenticationPrincipal User user, Model model,
                           @RequestParam String table) {
        betService.formModelForBets(model, userService.getUserById(user.getId()), table);
        return "bets";
    }

    @PostMapping(params = "tablePage")
    public String betsTable(@AuthenticationPrincipal User user, Model model,
                            @RequestParam(required = false) String tableName,
                            @RequestParam(required = false) int page) {
        User userFromDB = userService.getUserById(user.getId());
        Page<Bet> response = betService.getBetInfo(userFromDB, tableName, page);
        int totalPages = response.getTotalPages();
        List<Bet> items = betService.listFromPage(response);
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        model.addAttribute("items", items);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("tableName", tableName);
        model.addAttribute("currentPage", page);
        return "bets";
    }

    @GetMapping("/createBet")
    public String createBet(@AuthenticationPrincipal User user, Model model) {
        Map<String, Object> data = userService.getFriendsAll(user.getId());
        User userFromDB = (User) data.get("user");
        model.addAttribute("friends", data.get("friends"));
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        return "createBet";
    }

    @PostMapping("/createBet")
    public String createBet(@AuthenticationPrincipal User user, @RequestParam String game,
                            @RequestParam String gamemode, @RequestParam String value, @RequestParam String opponent,
                            @RequestParam String lobbyName, @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView("createBet");
        Map<String, Object> data = userService.getFriendsAll(user.getId());
        User userFromDB = (User) data.get("user");
        modelAndView.addObject("friends", data.get("friends"));
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

    @GetMapping("/{id}")
    public String getDetails(@AuthenticationPrincipal User user, Model model, @PathVariable Long id) {
        User userFromDB = userService.getUserById(user.getId());
        Bet bet = betService.getBet(id);
        UserService.ifAdmin(model, userFromDB);
        model.addAttribute("user", userFromDB);
        model.addAttribute("bet", bet);
        model.addAttribute("newMessages", userFromDB.isHaveNewMessages());
        model.addAttribute("newBets", userFromDB.isHaveNewBets());
        if (bet == null || betService.access(bet, userFromDB)) {
            return "errorPage";
        }
        betService.readNewBet(id, userFromDB);
        return "details";
    }

    @PostMapping(value = "/{id}", params = "confirmBet")
    public ModelAndView setConfirm(@AuthenticationPrincipal User user, ModelAndView modelAndView,
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

    @PostMapping(params = "deleteBet")
    public String deleteBet(@AuthenticationPrincipal User user, Model model,
                                    @RequestParam Long betId, @RequestParam String table) {
        User userFromDB = betService.deleteBet(betId, user.getId());
        betService.formModelForBets(model, userFromDB, table);
        return "bets";
    }
}
