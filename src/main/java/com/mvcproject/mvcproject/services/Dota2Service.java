package com.mvcproject.mvcproject.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvcproject.mvcproject.dto.LobbyDto;
import com.mvcproject.mvcproject.dto.ResponseData;
import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class Dota2Service {
    private String createUrl = "http://localhost:1347/createLobby";
    @Autowired
    private ObjectMapper objectMapper;

    public void createLobby(Bet bet) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        Game game = bet.getGame();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String uri = createUrl + "?lobbyName=" + game.getLobbyName();
        LobbyDto lobbyDto = new LobbyDto(game.getPassword(), game.getPlayer1(), game.getPlayer2());
        String request = objectMapper.writeValueAsString(lobbyDto);
        HttpEntity<String> requestBody = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(uri, requestBody, String.class);
        System.out.println();
    }
}
