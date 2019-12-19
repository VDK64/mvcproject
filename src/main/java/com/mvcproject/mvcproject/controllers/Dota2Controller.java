package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.Dota2.Dota2API;
import com.mvcproject.mvcproject.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Dota2Controller {
    @Autowired
    private Dota2API botService;
    public static final String token = "123"; /*UUID.randomUUID().toString()  while debug*/;
    private final String okStatus = "----------------------done----------------------";
    private final String accessDenied = "----------------------access denied----------------------";

    @PostMapping("/dota2/bot/start")
    public ResponseEntity<String> startLobby(@RequestBody ResponseData responseData) {
        if (responseData.getToken().equals(Dota2Controller.token)) {
            botService.startLobby(responseData.getUser(), responseData.getOpponent());
            return ResponseEntity.ok(okStatus);
        } else {
            return new ResponseEntity<>(accessDenied, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/dota2/bot/leave")
    public ResponseEntity<String> leaveLobby(@RequestBody ResponseData responseData) {
        if (responseData.getToken().equals(Dota2Controller.token)) {
            botService.leaveLobby(responseData.getUser(), responseData.getOpponent());
            return ResponseEntity.ok(okStatus);
        } else {
            return new ResponseEntity<>(accessDenied, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/dota2/bot/timeout")
        public ResponseEntity<String> lobbyTimeout(@RequestBody ResponseData responseData) {
            if (responseData.getToken().equals(Dota2Controller.token)) {
                botService.timeout(responseData.getUser(), responseData.getOpponent());
                return ResponseEntity.ok(okStatus);
            } else {
                return new ResponseEntity<>(accessDenied, HttpStatus.BAD_REQUEST);
            }
        }
}
