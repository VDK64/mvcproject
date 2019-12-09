package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.dota2.Dota2BotService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class Dota2Controller {
    @Autowired
    private Dota2BotService botService;
    public static final String token = "123"; /*UUID.randomUUID().toString()  while debug*/;

    @PostMapping("/dota2/bot/leave")
    public ResponseEntity<String> leaveLobby(@RequestBody String token) {
        if (token.equals(Dota2Controller.token)) {
            botService.setFree(true);
            return ResponseEntity.ok("done");
        } else {
            return new ResponseEntity<>("access denied", HttpStatus.BAD_REQUEST);
        }
    }
}
