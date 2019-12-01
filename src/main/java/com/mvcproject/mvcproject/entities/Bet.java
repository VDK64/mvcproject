package com.mvcproject.mvcproject.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bet")
@SequenceGenerator(name = "seqBet", allocationSize = 1)
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
    Boolean isConfirm;
    private String whoWin;
    @OneToOne(mappedBy = "bet", cascade = CascadeType.ALL,
            fetch = FetchType.EAGER/*, optional = false*/)
    private Game game;

    public Bet(Long id, User user, @NotNull Float value, User opponent, @NotNull Boolean isConfirm, String whoWin) {
        this.id = id;
        this.user = user;
        this.value = value;
        this.opponent = opponent;
        this.isConfirm = isConfirm;
        this.whoWin = whoWin;
    }
}
