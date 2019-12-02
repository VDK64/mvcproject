package com.mvcproject.mvcproject.dto;

import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetDto {
    private String user;
    private String opponent;
    private String game;
}
