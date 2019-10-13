package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    private Set<User> users = new HashSet<>();
    private long id = 0;

    @PostConstruct
    public void init() {
        users.add(User.builder()
                .id(++id)
                .username("user")
                .firstname("Ivan")
                .lastname("Petrov")
                .password(new BCryptPasswordEncoder().encode("password"))
                .authorities(Collections.singleton(Role.USER))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());
    }

    private void checkUserExsist(User user, String source)  {
        for (User user1 : users) {
            if (user1.getUsername().equals(user.getUsername())) {
                throw new CustomServerException(ServerErrors.ALREADY_EXIST, source);
            }
        }
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
        user.setId(++id);
        users.add(user);
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        return findUserFromSetByUsernameOrThrowUsernameNotFoundException(username);
    }

    private UserDetails findUserFromSetByUsernameOrThrowUsernameNotFoundException(String username) {
        final User[] user1 = {null};
        users.forEach(user -> {
            if (user.getUsername().equals(username)) {
                user1[0] = user;
            }
        });
        if (user1[0] == null) {
            throw new UsernameNotFoundException("user " + username + " was not found!");
        }
        return user1[0];
    }
}