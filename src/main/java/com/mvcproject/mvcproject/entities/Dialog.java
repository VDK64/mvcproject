package com.mvcproject.mvcproject.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = { "users", "messages" } )
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dlg")
@SequenceGenerator(name = "seqDialog", allocationSize = 1)
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqDialog")
    private Long id;
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name="dlg_usr",
            joinColumns = @JoinColumn(name="dlg_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name="usr_id", referencedColumnName="id")
    )
    private Set<User> users = new LinkedHashSet<>();
    @ToString.Exclude
    @OneToMany(mappedBy = "dialog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    private Boolean haveNewMessages;
}
