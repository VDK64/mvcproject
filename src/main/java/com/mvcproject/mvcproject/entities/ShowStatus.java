package com.mvcproject.mvcproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(exclude = { "dialog" } )
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shw_status")
@SequenceGenerator(name = "seqShw_status", allocationSize = 1, initialValue = 7)
public class ShowStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqShw_status")
    private Long id;
    @NotNull
    private String username;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "dlg_id")
    private Dialog dialog;
    private boolean visible;
}