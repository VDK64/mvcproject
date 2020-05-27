package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.dto.RequestData;
import com.mvcproject.mvcproject.services.Dota2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Dota2Controller {
    @Autowired
    private Dota2Service dota2Service;
    public static String token; /*UUID.randomUUID().toString() | 123 while debug*/
    private final String okStatus = "----------------------done----------------------";
    private final String accessDenied = "----------------------access denied----------------------";

    @Value("${token}")
    public void setToken(String token) {
        Dota2Controller.token = token;
    }

    @PostMapping("/dota2/bot/start")
    public ResponseEntity<String> startLobby(@RequestBody RequestData requestData) {
        if (requestData.getToken().equals(Dota2Controller.token)) {
            dota2Service.startLobby(requestData.getUser(), requestData.getOpponent());
            return ResponseEntity.ok(okStatus);
        } else {
            return new ResponseEntity<>(accessDenied, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/dota2/bot/leave")
    public ResponseEntity<String> leaveLobby(@RequestBody RequestData requestData) {
        if (requestData.getToken().equals(Dota2Controller.token)) {
            dota2Service.leaveLobby(requestData.getUser(), requestData.getOpponent());
            return ResponseEntity.ok(okStatus);
        } else {
            return new ResponseEntity<>(accessDenied, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/dota2/bot/positiveLeave")
    public ResponseEntity<String> positiveLeave(@RequestBody RequestData requestData) {
        if (requestData.getToken().equals(Dota2Controller.token)) {
            dota2Service.positiveLeave(requestData.getUser(), requestData.getOpponent());
            return ResponseEntity.ok(okStatus);
        } else {
            return new ResponseEntity<>(accessDenied, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/dota2/bot/timeout")
        public ResponseEntity<String> lobbyTimeout(@RequestBody RequestData requestData) {
            if (requestData.getToken().equals(Dota2Controller.token)) {
                dota2Service.timeout(requestData.getUser(), requestData.getOpponent());
                return ResponseEntity.ok(okStatus);
            } else {
                return new ResponseEntity<>(accessDenied, HttpStatus.BAD_REQUEST);
            }
        }
}
