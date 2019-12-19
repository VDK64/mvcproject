package com.mvcproject.mvcproject.Dota2;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.GameStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.BetRepo;
import com.mvcproject.mvcproject.repositories.GameRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.Dota2Service;
import com.mvcproject.mvcproject.validation.Validator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Getter
@Setter
public class Dota2API {
    private boolean isFree = true;
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GameRepo gameRepo;
    @Autowired
    private Validator validator;
    @Autowired
    private Dota2Service dota2Service;

    public synchronized void startLobby(String user, String opponent) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.STARTED);
        gameRepo.save(bet.getGame());
        isFree = false;
    }

    public void leaveLobby(String user, String opponent) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.LEAVE);
        gameRepo.save(bet.getGame());
        isFree = true;
    }

    public void timeout(String user, String opponent, String port) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.TIMEOUT);
        gameRepo.save(bet.getGame());
        isFree = true;
        for (Map.Entry<String, Boolean> bot : dota2Service.getBots().entrySet()) {
            if (bot.getKey().contains(port)) {
                bot.setValue(true);
                break;
            }
        }
    }

    private Bet getBetAndSetStatus(String user, String opponent, GameStatus gameStatus) {
        User userFromDB = userRepo.findBySteamId(user).orElseThrow();
        User opponentFromDB = userRepo.findBySteamId(opponent).orElseThrow();
        Bet bet = betRepo.findByUserAndOpponentAndWhoWin(userFromDB,
                opponentFromDB, null).orElseThrow();
        validator.validateStatus(bet.getGame(), gameStatus);
        bet.getGame().setStatus(gameStatus);
        return bet;
    }
}
