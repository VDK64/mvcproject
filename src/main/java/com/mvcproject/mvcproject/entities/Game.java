package com.mvcproject.mvcproject.entities;

import lombok.*;

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
}
