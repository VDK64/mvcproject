package com.mvcproject.mvcproject.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvcproject.mvcproject.dto.CreateLobbyDto;
import com.mvcproject.mvcproject.dto.LobbyDto;
import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.GameStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.InternalServerExceptions;
import com.mvcproject.mvcproject.repositories.BetRepo;
import com.mvcproject.mvcproject.repositories.GameRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class Dota2Service {
    @Value("${1347.createLobby}")
    private String localhost1347;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GameRepo gameRepo;
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private Validator validator;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private RestTemplate restTemplate;
    private Map<String, Boolean> bots;


    @PostConstruct
    private void init() {
        bots = new LinkedHashMap<>() {{
            put(localhost1347, true);
        }};
    }

    public synchronized void createLobby(Bet bet, String principal) throws JsonProcessingException {
        boolean created = false;
        do {
            for (Map.Entry<String, Boolean> bot : bots.entrySet()) {
                if (bot.getValue()) {
                    bot.setValue(false);
                    Game game = bet.getGame();
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    String uri = bot.getKey() + "?lobbyName=" + game.getLobbyName();
                    LobbyDto lobbyDto = new LobbyDto(game.getPassword(), game.getUserSteamId64(),
                            game.getOpponentSteamId64());
                    String request = objectMapper.writeValueAsString(lobbyDto);
                    HttpEntity<String> requestBody = new HttpEntity<>(request, headers);
                    ResponseEntity<CreateLobbyDto> response = restTemplate.postForEntity(uri, requestBody,
                            CreateLobbyDto.class);
                    if (Objects.requireNonNull(response.getBody()).getResponseCode().equals("100")) {
                        bot.setValue(true);
                        throw new InternalServerExceptions("startError", bet.getUser().getUsername(),
                                bet.getOpponent().getUsername(), principal);
                    }
                    created = true;
                    break;
                }
            }
        } while (!created);
    }

    public void startLobby(String user, String opponent) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.STARTED);
        bet.getGame().setServerStartTime(Math.toIntExact(System.currentTimeMillis() / 1000));
        gameRepo.save(bet.getGame());
    }

    public void leaveLobby(String user, String opponent) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.LEAVE);
        gameRepo.save(bet.getGame());
    }

    public void positiveLeave(String user, String opponent) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.POSITIVE_LEAVE);
        gameRepo.save(bet.getGame());
        bots.replace(localhost1347, true);
    }

    public void timeout(String user, String opponent) {
        Bet bet = getBetAndSetStatus(user, opponent, GameStatus.TIMEOUT);
        Game game = bet.getGame();
        game.setIsUserReady(false);
        game.setIsOpponentReady(false);
        gameRepo.save(bet.getGame());
        bots.replace(localhost1347, true);
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

    public Map<String, Boolean> getBots() {
        return bots;
    }

    public void setBots(Map<String, Boolean> bots) {
        this.bots = bots;
    }
}
