package com.mvcproject.mvcproject.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "from_id"),
            @JoinColumn(name = "to_id")
    })
    private Dialog dialog;

}
