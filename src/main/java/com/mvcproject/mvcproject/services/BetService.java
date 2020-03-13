package com.mvcproject.mvcproject.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mvcproject.mvcproject.dto.BetDto;
import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.GameStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.BetRepo;
import com.mvcproject.mvcproject.repositories.GameRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import freemarker.template.utility.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
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
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private Dota2Service dota2Service;
    @Autowired
    private MessageService messageService;
    @Autowired
    private BetService betService;

    public Page<Bet> getBetInfo(User user, String who) {
        if (who.equalsIgnoreCase("owner"))
            return betRepo.findByUser(user, PageRequest.of(0, 10));
        if (who.equalsIgnoreCase("opponent"))
            return betRepo.findByOpponent(user, PageRequest.of(0, 10));
        else throw new CustomServerException(ServerErrors.WRONG_QUERY, null);
    }

    public Page<Bet> getBetInfo(User user, String who, int offset) {
        if (who.equalsIgnoreCase("user"))
            return betRepo.findByUser(user, PageRequest.of(offset - 1, 10));
        if (who.equalsIgnoreCase("opponent"))
            return betRepo.findByOpponent(user, PageRequest.of(offset - 1, 10));
        else throw new CustomServerException(ServerErrors.WRONG_QUERY, null);
    }

    public void createBetAndGame(User user, String game, String gamemode, String value, String opponent,
                                 String lobbyName, String password, ModelAndView modelAndView) {
        String opponentUsername = opponent.split(" ")[1];
        User opponentFromDB = userRepo.findByUsername(opponentUsername).orElseThrow();
        Float floatValue = validateDataToCreateBetAndGame(user, value, modelAndView, opponentFromDB, lobbyName
                , password);
        Game katka = new Game(null, lobbyName, password, gamemode, false, false,
                user.getSteamId(), opponentFromDB.getSteamId());
        gameRepo.save(katka);
        Bet bet = new Bet(null, user, floatValue,
                opponentFromDB, false, null,
                katka, true);
        betRepo.save(bet);
        user.setDeposit(user.getDeposit() - floatValue);
        userRepo.save(user);
        template.convertAndSendToUser(opponentUsername, "/queue/events", new BetDto(user.getUsername(),
                opponentUsername, game, null));
    }

    private Float validateDataToCreateBetAndGame(User user, String value, ModelAndView modelAndView,
                                                 User opponentFromDB, String lobbyName, String password) {
        if (StringUtil.emptyToNull(lobbyName) == null) {
            throw new CustomServerException(ServerErrors.LOBBYNAME_NULL, modelAndView);
        }
        if (StringUtil.emptyToNull(password) == null) {
            throw new CustomServerException(ServerErrors.LOBBYPASSWORD_NULL, modelAndView);
        }
        betRepo.findByUserAndOpponentAndWhoWin(user, opponentFromDB, null).ifPresent(bet -> {
            throw new CustomServerException(ServerErrors.BET_EXIST, modelAndView);
        });
        betRepo.findByUserAndOpponentAndWhoWin(opponentFromDB, user, null).ifPresent(bet -> {
            throw new CustomServerException(ServerErrors.BET_EXIST, modelAndView);
        });
        Float floatValue = validator.validValueAndConvertToFlat(value, modelAndView);
        if (floatValue > user.getDeposit() || floatValue > opponentFromDB.getDeposit()) {
            throw new CustomServerException(ServerErrors.WRONG_BET_VALUE, modelAndView);
        }
        if (floatValue == 0f) {
            throw new CustomServerException(ServerErrors.WRONG_VALUE, modelAndView);
        }
        return floatValue;
    }

    public void readNewBet(Long id) {
        Bet betFromDB = betRepo.findById(id).orElseThrow();
        betFromDB.setIsNew(false);
        betRepo.save(betFromDB);
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

    @Transactional
    public boolean haveNewBets(User user) {
        return !betRepo.findByOpponentAndIsNew(user, true).isEmpty();
    }

    public void betReady(User user, BetDto betDto) throws JsonProcessingException {
        Bet betFromDB = betRepo.findById(betDto.getId()).orElseThrow();
        betFromDB.setIsNew(true);
        Game game = betFromDB.getGame();
        if (betDto.getUser().equals(user.getUsername())) {
            game.setIsUserReady(true);
        } else {
            game.setIsOpponentReady(true);
        }
        if (game.getIsUserReady() && game.getIsOpponentReady()) {
            betDto.setInfo("allReady");
            if (game.getStatus() != GameStatus.STARTED)
                dota2Service.createLobby(betFromDB);
            template.convertAndSendToUser(detectDestinationUsername(user, betDto), "/queue/events", betDto);
            betDto.setInfo("startLobby");
            template.convertAndSendToUser(betDto.getUser(), "/queue/events", betDto);
            template.convertAndSendToUser(betDto.getOpponent(), "/queue/events", betDto);
        } else {
            template.convertAndSendToUser(detectDestinationUsername(user, betDto), "/queue/events", betDto);
        }
        gameRepo.save(game);
        betRepo.save(betFromDB);
    }

    public void betInfo(BetDto betDto) {
        template.convertAndSendToUser(betDto.getUser(), "/queue/events", betDto);
        template.convertAndSendToUser(betDto.getOpponent(), "/queue/events", betDto);
    }

    private String detectDestinationUsername(User user, BetDto betDto) {
        if (betDto.getUser().equals(user.getUsername()))
            return betDto.getOpponent();
        else return betDto.getUser();
    }

    public Bet getBet(Long id) {
        return betRepo.findById(id).orElse(null);
    }

    public boolean access(Bet bet, User user) {
        return !bet.getUser().getUsername().equals(user.getUsername())
                && !bet.getOpponent().getUsername().equals(user.getUsername());
    }

    public Bet setConfirm(Long id, User user, ModelAndView modelAndView) {
        Bet betFromDB = betRepo.findById(id).orElseThrow();
        validator.validateAndSetDepositAfterBetTaking(user, betFromDB.getValue(), modelAndView);
        user.setDeposit(user.getDeposit() - betFromDB.getValue());
        betFromDB.setIsConfirm(true);
        betRepo.save(betFromDB);
        userRepo.save(user);
        return betFromDB;
    }
}
