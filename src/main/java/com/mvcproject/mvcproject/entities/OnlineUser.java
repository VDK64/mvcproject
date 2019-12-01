package com.mvcproject.mvcproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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