package com.mvcproject.mvcproject.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mvcproject.mvcproject.config.MvcConfig;
import com.mvcproject.mvcproject.dto.BetDto;
import com.mvcproject.mvcproject.dto.matchResult.MatchResultDto;
import com.mvcproject.mvcproject.dto.matchResult.Player;
import com.mvcproject.mvcproject.dto.specialMatchData.SpecialMatchDataDto;
import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.GameStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.InternalServerExceptions;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.BetRepo;
import com.mvcproject.mvcproject.repositories.GameRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private static final String USER = "USER";
    private static final String OPPONENT = "OPPONENT";
    private static final String READY_ERROR = "readyError";
    private static final String START_ERROR = "startError";

    public Page<Bet> getBetInfo(User user, String who) {
        if (who.equalsIgnoreCase("owner"))
            return betRepo.findByUser(user, PageRequest.of(0, 10));
        if (who.equalsIgnoreCase("opponent"))
            return betRepo.findByOpponent(user, PageRequest.of(0, 10));
        else throw new CustomServerException(ServerErrors.WRONG_QUERY, null);
    }

    public Page<Bet> getBetInfo(User user, String who, int offset) {
        if (who.equalsIgnoreCase("owner"))
            return betRepo.findByUser(user, PageRequest.of(offset - 1, 10));
        if (who.equalsIgnoreCase("opponent"))
            return betRepo.findByOpponent(user, PageRequest.of(offset - 1, 10));
        else throw new CustomServerException(ServerErrors.WRONG_QUERY, null);
    }

    public void createBetAndGame(User user, String game, String gamemode, String value, String opponent,
                                 String lobbyName, String password, ModelAndView modelAndView) {
        String opponentUsername = opponent.split(" ")[1];
        User opponentFromDB = userRepo.findByUsername(opponentUsername).orElseThrow();
        Float floatValue = validator.validateDataToCreateBetAndGame(user, value, modelAndView, opponentFromDB,
                lobbyName, password);
        Game katka = new Game(null, lobbyName, password, gamemode, false, false,
                user.getSteamId(), opponentFromDB.getSteamId());
        Bet bet = new Bet(null, user, floatValue,
                opponentFromDB, false, null,
                katka, true);
        betRepo.save(bet);
        user.setDeposit(user.getDeposit() - floatValue);
        opponentFromDB.setHaveNewBets(true);
        userRepo.save(opponentFromDB);
        userRepo.save(user);
        template.convertAndSendToUser(opponentUsername, "/queue/events", new BetDto(user.getUsername(),
                opponentUsername, game, null));
    }

    public void readNewBet(Long id, User user) {
        Bet betFromDB = betRepo.findById(id).orElseThrow();
        betFromDB.setIsNew(false);
        user.setHaveNewBets(false);
        userRepo.save(user);
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
            checkReadyGames(user, betFromDB.getOpponent(), USER);
            game.setIsUserReady(true);
        } else {
            checkReadyGames(betFromDB.getUser(), user, OPPONENT);
            game.setIsOpponentReady(true);
        }
        if (game.getIsUserReady() && game.getIsOpponentReady()) {
            betDto.setInfo("allReady");
            if (game.getStatus() != GameStatus.STARTED)
                dota2Service.createLobby(betFromDB, user.getUsername());
            user.setHaveNewBets(true);
            gameRepo.save(game);
            betRepo.save(betFromDB);
            userRepo.save(user);
            template.convertAndSendToUser(detectDestinationUsername(user, betDto), "/queue/events", betDto);
            betDto.setInfo("startLobby");
            template.convertAndSendToUser(betDto.getUser(), "/queue/events", betDto);
            template.convertAndSendToUser(betDto.getOpponent(), "/queue/events", betDto);
        } else {
            user.setHaveNewBets(true);
            gameRepo.save(game);
            betRepo.save(betFromDB);
            userRepo.save(user);
            template.convertAndSendToUser(detectDestinationUsername(user, betDto), "/queue/events", betDto);
        }
    }

    private void checkReadyGames(User user, User opponent, String who) {
        if (who.equals(USER)) {
            List<Game> list = gameRepo.findByUserSteamId64AndIsUserReady(user.getSteamId(), true);
            if (!list.isEmpty()) {
                throw new InternalServerExceptions(READY_ERROR, user.getUsername(),
                        opponent.getUsername(), user.getUsername());
            }
        } else {
            List<Game> list = gameRepo.findByOpponentSteamId64AndIsOpponentReady(user.getSteamId(), true);
            if (!list.isEmpty()) {
                throw new InternalServerExceptions(READY_ERROR, user.getUsername(),
                        opponent.getUsername(), opponent.getUsername());
            }
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

    public void formModelForBets(Model model, User user, String table) {
        Page<Bet> response = betService.getBetInfo(user, table);
        int totalPages = response.getTotalPages();
        List<Bet> items = betService.listFromPage(response);
        UserService.ifAdmin(model, user);
        model.addAttribute("user", user);
        model.addAttribute("newMessages", user.isHaveNewMessages());
        model.addAttribute("newBets", user.isHaveNewBets());
        model.addAttribute("items", items);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("tableName", table);
        model.addAttribute("currentPage", 1);
    }

    public Bet setConfirm(Long id, User user, ModelAndView modelAndView) {
        Bet betFromDB = betRepo.findById(id).orElseThrow();
        validator.validateToSetConfirm(user, betFromDB.getValue(), modelAndView);
        user.setDeposit(user.getDeposit() - betFromDB.getValue());
        betFromDB.setIsConfirm(true);
        betRepo.save(betFromDB);
        userRepo.save(user);
        return betFromDB;
    }

    @Transactional
    public User deleteBet(Long betId, Long userId) {
        User user = userService.getUserById(userId);
        Set<Bet> bets = user.getBets();
        bets.stream()
                .filter(bet -> bet.getId().equals(betId) && !bet.getIsConfirm())
                .findFirst()
                .ifPresent(bet -> {
                    user.setDeposit(user.getDeposit() + bet.getValue());
                    bets.remove(bet);
                    userRepo.save(user);
                });
        return user;
    }

    public void messageParser(User user, BetDto betDto) throws JsonProcessingException {
        if (betDto.getInfo().equals("check"))
            betService.checkGames(user, betDto);
        else
            betService.betReady(user, betDto);
    }

    public void checkGames(User principal, BetDto betDto) {
        Bet bet = betRepo.findById(betDto.getId()).orElseThrow();
        checkWhoWinInGame(principal, bet);
    }

    private void checkWhoWinInGame(User principal, Bet bet) {
        Game game = bet.getGame();
        User user = userRepo.findBySteamId(game.getUserSteamId64()).orElseThrow();
        User opponent = userRepo.findBySteamId(game.getOpponentSteamId64()).orElseThrow();
        BetDto betDto = new BetDto(bet.getId(), user.getUsername(), opponent.getUsername(),
                null, "closeBet");
        Map<String, String> response = makeRequestToFindMatch(game);
        if (response.size() == 0) {
            throw new InternalServerExceptions(START_ERROR, bet.getUser().getUsername(),
                    bet.getOpponent().getUsername(), principal.getUsername());
        } else {
            if (isWinnerUser(response, game, user, opponent, bet)) {
                user.setDeposit(user.getDeposit() + bet.getValue());
                bet.setWhoWin(user.getUsername());
            } else {
                opponent.setDeposit(opponent.getDeposit() + bet.getValue());
                bet.setWhoWin(opponent.getUsername());
            }
        }
        bet.setGame(null);
        userRepo.save(user);
        userRepo.save(opponent);
        betRepo.save(bet);
        template.convertAndSendToUser(bet.getUser().getUsername(), "/queue/events", betDto);
        template.convertAndSendToUser(bet.getOpponent().getUsername(), "/queue/events", betDto);
    }

    private boolean isWinnerUser(Map<String, String> response, Game game, User user, User opponent, Bet bet) {
        String match_id = response.get("match_id");
        String radiant = response.get("radiant").trim();
        String steamId32 = String.valueOf(userService.convertSteamIdTo32(user.getSteamId()));
        String url = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/" +
                "?match_id=" + match_id + "&key=" + MvcConfig.steamKey;
        ResponseEntity<SpecialMatchDataDto> responseTemplate = restTemplate.getForEntity(url,
                SpecialMatchDataDto.class);
        SpecialMatchDataDto responseBody = responseTemplate.getBody();
        return Objects.requireNonNull(responseBody).getResult().isRadiant_win() && steamId32.equals(radiant);
    }

    private Map<String, String> makeRequestToFindMatch(Game game) {
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
        List<?> players = Arrays.asList((Player[]) result.get("players"));
        StringBuilder radiantSteamId32 = new StringBuilder();
        Map<String, String> resultMap = new HashMap<>();
        if (checkPlayersInListAndDifferentTeam(players, String.valueOf(userSteamId32),
                String.valueOf(opponentSteamId32), radiantSteamId32) && defineIsMatchStartAfterCreate(result, game)) {
            resultMap.put("match_id", String.valueOf(result.get("match_id")));
            resultMap.put("radiant", radiantSteamId32.toString());
        }
        return resultMap;
    }

    private boolean defineIsMatchStartAfterCreate(Map<String, Object> result, Game game) {
        int startTime = Integer.parseInt((String) result.get("start_time"));
        return game.getServerStartTime() < startTime;
    }

    private boolean checkPlayersInListAndDifferentTeam(List<?> players, String userAccount, String opponentAccount,
                                                       StringBuilder radiantSteamID32) {
        AtomicInteger radiant = new AtomicInteger();
        AtomicInteger count = new AtomicInteger();
        players.forEach(player -> {
            if (player instanceof Player && ((Player) player).getAccount_id() != null
                    && (((Player) player).getAccount_id().equals(userAccount)
                    || ((Player) player).getAccount_id().equals(opponentAccount))) {
                count.getAndIncrement();
                if (0 <= Integer.parseInt(((Player) player).getPlayer_slot())
                        && Integer.parseInt(((Player) player).getPlayer_slot()) < 128) {
                    radiant.getAndIncrement();
                    radiantSteamID32.append(((Player) player).getAccount_id());
                }
            }
        });
        return count.get() == 2 && radiant.get() < 2 && radiant.get() > 0;
    }

    public String whoPrincipal(String username, String principal) {
        if (username.equals(principal))
            return "user";
        else
            return "opponent";
    }
}
