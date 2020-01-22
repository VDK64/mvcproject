package com.mvcproject.mvcproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

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
    private String text;
    @NotNull
    private Date date;
    @NotNull
    private Long fromId;
    @NotNull
    private Long toId;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dlg_id")
    private Dialog dialog;
    private Boolean newMessage;
}