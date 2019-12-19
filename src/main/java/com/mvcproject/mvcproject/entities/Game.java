package com.mvcproject.mvcproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "game")
@SequenceGenerator(name = "seqGame", allocationSize = 1)
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGame")
    private Long id;
    private String lobbyName;
    private String password;
    private String gameMode;
    private Boolean isUserReady;
    private  Boolean isOpponentReady;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GameStatus status;
    private String player1;
    private String player2;

    public Game(Long id, String lobbyName, String password, String gameMode, Boolean isUserReady,
                Boolean isOpponentReady, String player1, String player2) {
        this.id = id;
        this.lobbyName = lobbyName;
        this.password = password;
        this.gameMode = gameMode;
        this.isUserReady = isUserReady;
        this.isOpponentReady = isOpponentReady;
        this.player1 = player1;
        this.player2 = player2;
    }
}
