package com.mvcproject.mvcproject.dota2;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.GameStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.BetRepo;
import com.mvcproject.mvcproject.repositories.GameRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class Dota2BotService {
    private boolean isFree = true;
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GameRepo gameRepo;

    public void startLobby(String user, String opponent) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.STARTED);
        gameRepo.save(bet.getGame());
        isFree = false;
    }

    public void leaveLobby(String user, String opponent) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.LEAVE);
        gameRepo.save(bet.getGame());
        isFree = true;
    }

    public void timeout(String user, String opponent) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.TIMEOUT);
        gameRepo.save(bet.getGame());
        isFree = true;
    }

    private Bet getBetAndSetStatus(String user, String opponent, GameStatus gameStatus) {
        User userFromDB = userRepo.findBySteamId(user).orElseThrow();
        User opponentFromDB = userRepo.findBySteamId(opponent).orElseThrow();
        Bet bet = betRepo.findByUserAndOpponentAndWhoWin(userFromDB,
                opponentFromDB, null).orElseThrow();
        bet.getGame().setStatus(gameStatus);
        return bet;
    }

}
