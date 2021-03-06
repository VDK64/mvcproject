package com.mvcproject.mvcproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"game", "user", "opponent"})
@Entity
@Table(name = "bet")
@SequenceGenerator(name = "seqBet", allocationSize = 1, initialValue = 66)
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqBet")
    private Long id;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name= "usr_id")
    private User user;
    @NotNull
    private Float value;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name= "opponent_id")
    private User opponent;
    @NotNull
    private Boolean isConfirm;
    private String whoWin;
    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "game_id")
    private Game game;
    //must be not null. must do it later
    @NotNull
    private Boolean isNew;
}
