package com.mvcproject.mvcproject.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "onln_usr")
@SequenceGenerator(name = "seqonln_usr", allocationSize = 1)
public class OnlineUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqonln_usr")
    private Long id;
    private String username;
}