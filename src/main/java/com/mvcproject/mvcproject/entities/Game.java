package com.mvcproject.mvcproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

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

    public Game(Long id, String lobbyName, String password, String gameMode, Boolean isUserReady, Boolean isOpponentReady) {
        this.id = id;
        this.lobbyName = lobbyName;
        this.password = password;
        this.gameMode = gameMode;
        this.isUserReady = isUserReady;
        this.isOpponentReady = isOpponentReady;
    }
}
