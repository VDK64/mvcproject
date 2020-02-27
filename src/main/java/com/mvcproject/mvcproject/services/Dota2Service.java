package com.mvcproject.mvcproject.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvcproject.mvcproject.dto.CreateLobbyDto;
import com.mvcproject.mvcproject.dto.LobbyDto;
import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class Dota2Service {
    @Value("${1347.createLobby}")
    private String localhost1347;
    @Autowired
    private ObjectMapper objectMapper;
    private RestTemplate restTemplate;
    private Map<String, Boolean> bots;
    private boolean created;

    @PostConstruct
    private void init() {
        bots = new LinkedHashMap<>() {{
            put(localhost1347, true);
        }};
        restTemplate = new RestTemplate();
    }

    public synchronized void createLobby(Bet bet) throws JsonProcessingException {
        created = false;
        do {
            for (Map.Entry<String, Boolean> bot : bots.entrySet()) {
                if (bot.getValue()) {
                    Game game = bet.getGame();
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    String uri = bot.getKey() + "?lobbyName=" + game.getLobbyName();
                    LobbyDto lobbyDto = new LobbyDto(game.getPassword(), game.getPlayer1(), game.getPlayer2());
                    String request = objectMapper.writeValueAsString(lobbyDto);
                    HttpEntity<String> requestBody = new HttpEntity<>(request, headers);
                    ResponseEntity<CreateLobbyDto> response = restTemplate.postForEntity(uri, requestBody, CreateLobbyDto.class);
                    parseResponse(response);
                    bot.setValue(false);
                    created = true;
                    break;
                }
            }
        } while (!created);
    }

    private void parseResponse(ResponseEntity<CreateLobbyDto> response) {
        System.out.println(response);
    }

    public Map<String, Boolean> getBots() {
        return bots;
    }

    public void setBots(Map<String, Boolean> bots) {
        this.bots = bots;
    }
}
