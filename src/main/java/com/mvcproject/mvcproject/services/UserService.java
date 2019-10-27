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
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.*;
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

    private void checkUserExsist(User user, ModelAndView model) {
        userRepo.findByUsername(user.getUsername()).
                ifPresent( user1 -> {
                    if (!user1.getId().equals(user.getId()))
                    throw new CustomServerException(ServerErrors.ALREADY_EXIST, model);
                });
    }

    public void createUser(String firstname, String lastname, String username, String password, ModelAndView model)
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
        checkUserExsist(user, model);
        userRepo.save(user);
    }

    public void changeUser(User user, String firstname, String lastname, String username, String password,
                           Map<String, String> authorities, ModelAndView model) {
        Map<String, String> map = checkUserField(firstname, lastname, username, password, user);
        model.addObject("username", user.getUsername());
        model.addObject("authorities", Role.values());
        Set<Role> roles = new LinkedHashSet<>();
        authorities.forEach((s1, s2) -> {
            if (s1.contains("authority")) { roles.add(Role.valueOf(s2)); }
        });
        user.setFirstname(map.get("firstname"));
        user.setLastname(map.get("lastname"));
        user.setUsername(map.get("username"));
        checkUserExsist(user, model);
        user.setPassword(map.get("password"));
        user.setAuthorities(roles);
        userRepo.save(user);
    }

    private Map<String, String> checkUserField(String firstname, String lastname, String username, String password, User user) {
        Map<String, String> res = new LinkedHashMap<>();
        if (firstname == null || firstname.equals("")) {
            res.put("firstname", user.getFirstname());
        } else { res.put("firstname", firstname); }
        if (lastname == null || lastname.equals("")) {
            res.put("lastname", user.getLastname());
        } else { res.put("lastname", lastname); }
        if (username == null || username.equals("")) {
            res.put("username", user.getUsername());
        } else { res.put("username", username); }
        if (password == null || password.equals("")) {
            res.put("password", user.getPassword());
        } else { res.put("password", new BCryptPasswordEncoder().encode(password)); }
        return res;
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