package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.BetRepo;
import com.mvcproject.mvcproject.repositories.GameRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BetService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private GameRepo gameRepo;
    @Autowired
    private Validator validator;

    public Page<Bet> getBetInfo(User user, String who) {
        if (who.equalsIgnoreCase("user"))
            return betRepo.findByUser(user, PageRequest.of(0, 10));
        if (who.equalsIgnoreCase("opponent"))
            return betRepo.findByOpponent(user, PageRequest.of(0, 10));
        else throw new CustomServerException(ServerErrors.WRONG_QUERY, null);
    }

    public Page<Bet> getBetInfo(User user, String who, int offset) {
        if (who.equalsIgnoreCase("user"))
            return betRepo.findByUser(user, PageRequest.of(offset-1, 10));
        if (who.equalsIgnoreCase("opponent"))
            return betRepo.findByOpponent(user, PageRequest.of(offset-1, 10));
        else throw new CustomServerException(ServerErrors.WRONG_QUERY, null);
    }

    public Bet createBetAndGame(User user, String game, String gamemode, String value, String opponent,
                                String lobbyName, String password) {
        Game katka = new Game(null, lobbyName, password, gamemode);
        Game save = gameRepo.save(katka);
        Bet bet = new Bet(null, user, Float.valueOf(value),
                userRepo.findByUsername(opponent.split(" ")[1]).orElseThrow(), false, null,
                katka);
        return betRepo.save(bet);
    }

    public List<Bet> listFromPage(Page<Bet> data) {
        List<Bet> response = data.get().collect(Collectors.toList());
        sortBets(response);
        return response;
    }

    private void sortBets(List<Bet> data) {
         data.sort((o1, o2) -> {
             if (o1.getWhoWin() == null)
                 return -1;
             else if (o2.getWhoWin() == null)
                 return 1;
             else return 0;
         });
    }
}
