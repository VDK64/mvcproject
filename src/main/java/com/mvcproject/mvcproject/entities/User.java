package com.mvcproject.mvcproject.entities;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = { "dialogs", "bets", "betsOpponent", "friends" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usr")
@SequenceGenerator(name = "seqUser", allocationSize = 1, initialValue = 8)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUser")
    private Long id;
    @NotNull
    private String firstname;
    @NotNull
    private String lastname;
    @NotNull
    @Column(unique = true)
    private String username;
    private String email;
    private String activationCode;
    private String avatar;
    @ElementCollection(fetch = FetchType.EAGER, targetClass = Role.class)
    @CollectionTable(name = "roleAuthorities", joinColumns = @JoinColumn(name = "authorities_id"))
    @Column(name = "authorities", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> authorities;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    private Set<Dialog> dialogs = new LinkedHashSet<>();
    @ToString.Exclude
    @ManyToMany()
    @JoinTable(name = "usr_fr",
            joinColumns =
            @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns =
            @JoinColumn(name = "friend_id", referencedColumnName = "id"))
    private Set<User> friends = new HashSet<>();
    @NotNull
    private Float deposit;
    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Bet> bets = new LinkedHashSet<>();
    @ToString.Exclude
    @OneToMany(mappedBy = "opponent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Bet> betsOpponent = new LinkedHashSet<>();
    @NotNull
    private Boolean isOnline;
    private String steamId;
    private boolean haveNewMessages;
    private boolean haveNewBets;
}
