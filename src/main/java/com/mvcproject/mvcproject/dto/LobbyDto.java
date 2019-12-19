package com.mvcproject.mvcproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LobbyDto {
    private String password;
    private String player1;
    private String player2;
}
