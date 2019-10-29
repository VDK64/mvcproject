package com.mvcproject.mvcproject.entities;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Data
@EqualsAndHashCode(exclude = { "dialogs" } )
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usr")
@SequenceGenerator(name = "seqUser", allocationSize = 1)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqUser")
    private Long id;
    @NotNull
    private String firstname;
    @NotNull
    private String lastname;
    @NotNull
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
}
