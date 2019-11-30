package com.mvcproject.mvcproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
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
}
