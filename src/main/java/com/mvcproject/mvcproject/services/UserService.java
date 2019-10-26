package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.UserRepo;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @PostConstruct
    public void init() {
        userRepo.save(User.builder()
                .username("user")
                .firstname("Ivan")
                .lastname("Petrov")
                .password(new BCryptPasswordEncoder().encode("p"))
                .authorities(Stream.of(Role.USER, Role.ADMIN).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());
    }

    private void checkUserExsist(User user, String source) {
        userRepo.findByUsername(user.getUsername()).
                ifPresent( user1 -> { throw new CustomServerException(ServerErrors.ALREADY_EXIST, source); });
    }

    public void createUser(String firstname, String lastname, String username, String password, String source)
            throws CustomServerException {
        User user = (User.builder()
                .username(username)
                .firstname(firstname)
                .lastname(lastname)
                .password(new BCryptPasswordEncoder().encode(password))
                .authorities(Collections.singleton(Role.USER))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());
        checkUserExsist(user, source);
        userRepo.save(user);
    }

    public void changeUser(User user, String firstname, String lastname, String username, String password,
                           Map<String, String> authorities) {
        Set<Role> roles = new LinkedHashSet<>();
        authorities.forEach((s1, s2) -> {
            if (s1.equals("authorities")) { roles.add(Role.valueOf(s2)); }
        });
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setUsername(username);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setAuthorities(roles);
        userRepo.save(user);
    }

    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        return userRepo.findByUsername(username).orElseThrow( () -> {
            throw new UsernameNotFoundException("user " + username + " was not found!");} );
    }
}