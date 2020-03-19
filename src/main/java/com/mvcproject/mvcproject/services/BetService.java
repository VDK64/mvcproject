package com.mvcproject.mvcproject.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mvcproject.mvcproject.config.MvcConfig;
import com.mvcproject.mvcproject.dto.BetDto;
import com.mvcproject.mvcproject.dto.matchResult.MatchResultDto;
import com.mvcproject.mvcproject.dto.matchResult.Player;
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
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.*;
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
    @Autowired
    private UserService userService;
    @Autowired
    private RestTemplate restTemplate;

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
        gameRepo.save(game);
        betRepo.save(betFromDB);
        if (game.getIsUserReady() && game.getIsOpponentReady()) {
            betDto.setInfo("allReady");
            if (game.getStatus() != GameStatus.STARTED)
                dota2Service.createLobby(betFromDB, user.getUsername());
            template.convertAndSendToUser(detectDestinationUsername(user, betDto), "/queue/events", betDto);
            betDto.setInfo("startLobby");
            template.convertAndSendToUser(betDto.getUser(), "/queue/events", betDto);
            template.convertAndSendToUser(betDto.getOpponent(), "/queue/events", betDto);
        } else {
            template.convertAndSendToUser(detectDestinationUsername(user, betDto), "/queue/events", betDto);
        }
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

    public String detectDestinationNotPrincipal(String principal, BetDto betDto) {
        if (betDto.getUser().equals(principal))
            return betDto.getOpponent();
        else
            return betDto.getUser();
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

    //    @Scheduled(cron = "0 0/5 * * * *")
    @PostConstruct
    private void checkGames() {
        List<Game> result = gameRepo.findByStatus(GameStatus.POSITIVE_LEAVE);
        if (result.size() != 0) {
            result.forEach(this::checkWhoWinInGame);
        }
    }

    private void checkWhoWinInGame(Game game) {
        Bet bet = betRepo.findByGame(game).orElseThrow();
        User user = userRepo.findBySteamId(game.getUserSteamId64()).orElseThrow();
        User opponent = userRepo.findBySteamId(game.getOpponentSteamId64()).orElseThrow();
        BetDto betDto = new BetDto(null, null, null, "closeBet");
        String response = makeRequestToFindMatch(game);
        if (response == null) {
            template.convertAndSendToUser(bet.getUser().getUsername(), "/queue/events", betDto);
            template.convertAndSendToUser(bet.getOpponent().getUsername(), "/queue/events", betDto);
            user.setDeposit(user.getDeposit() + bet.getValue());
            opponent.setDeposit(opponent.getDeposit() + bet.getValue());
        } else {

        }
        userRepo.save(user);
        userRepo.save(opponent);
        betRepo.delete(bet);
    }

    private String makeRequestToFindMatch(Game game) {
        int userSteamId32 = userService.convertSteamIdTo32(game.getUserSteamId64());
        int opponentSteamId32 = userService.convertSteamIdTo32(game.getOpponentSteamId64());
        String uri = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001" +
                "?league_id=11529&matches_requested=1&account_id=" + userSteamId32 +
                "&key=" + MvcConfig.steamKey;
        ResponseEntity<MatchResultDto> response = restTemplate.getForEntity(uri, MatchResultDto.class);
        MatchResultDto responseBody = response.getBody();
        Map<String, Object> result = new HashMap<>();
        if (responseBody != null) {
            result.put("start_time", responseBody.getResult().getMatches()[0].getStart_time());
            result.put("match_id", responseBody.getResult().getMatches()[0].getMatch_id());
            result.put("players", responseBody.getResult().getMatches()[0].getPlayers());
        }
        Player[] playersArray = (Player[]) result.get("players");
        List<?> players = Arrays.asList(playersArray);
        if (checkPlayersInListAndDifferentTeam(players, String.valueOf(userSteamId32),
                String.valueOf(opponentSteamId32)) && defineMatch(result, game)) {
            return (String) result.get("match_id");
        }
        return null;
    }

    private boolean defineMatch(Map<String, Object> result, Game game) {
        int startTime = Integer.parseInt((String) result.get("start_time"));
        return game.getServerStartTime() < startTime;
    }

    private boolean checkPlayersInListAndDifferentTeam(List<?> players, String userAccount, String opponentAccount) {
        int count = (int) players.stream().filter(player -> player instanceof Player
                && ((Player) player).getAccount_id() != null
                && (((Player) player).getAccount_id().equals(userAccount)
                || ((Player) player).getAccount_id().equals(opponentAccount))).count();
        return count == 2;
    }
}
