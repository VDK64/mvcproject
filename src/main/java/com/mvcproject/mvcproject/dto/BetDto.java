package com.mvcproject.mvcproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetDto {
    private Long id;
    private String user;
    private String opponent;
    private String game;
    private String info;

    public BetDto(String user, String opponent, String game, String info) {
        this.user = user;
        this.opponent = opponent;
        this.game = game;
        this.info = info;
    }
}
