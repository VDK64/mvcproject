package com.mvcproject.mvcproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(exclude = { "dialog" } )
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "msg")
@SequenceGenerator(name = "seqMessage", allocationSize = 1)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqMessage")
    private Long id;
    @NotNull
    @ToString.Exclude
    private String text;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dlg_id")
    private Dialog dialog;
}
