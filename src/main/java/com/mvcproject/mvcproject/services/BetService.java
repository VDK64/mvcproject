package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.BetRepo;
import com.mvcproject.mvcproject.repositories.GameRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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

    public List<List<Bet>> getBetInfo(User user) {
        List<List<Bet>> response = new ArrayList<>();
        List<Bet> byUser = betRepo.findByUser(user);
        List<Bet> byOpponent = betRepo.findByOpponent(user);
        response.add(byUser);
        response.add(byOpponent);
        return response;
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
}
