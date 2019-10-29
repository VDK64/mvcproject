package com.mvcproject.mvcproject.data;

import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataBaseCreate {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DialogRepo dialogRepo;

    public void formUsersInDataBase() {
        userRepo.save(User.builder()
                .username("vdk64")
                .firstname("Дмитрий")
                .lastname("Вознюк")
                .password(new BCryptPasswordEncoder().encode("a"))
                .email("dkvoznyuk@yandex.ru")
                .activationCode(null)
                .authorities(Stream.of(Role.USER, Role.ADMIN).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());

        userRepo.save(User.builder()
                .username("user")
                .firstname("Ivan")
                .lastname("Petrov")
                .password(new BCryptPasswordEncoder().encode("a"))
            .email("user@mail.ru")
                .activationCode(null)
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
            .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());

        userRepo.save(User.builder()
                .username("kasha111")
                .firstname("Аркадий")
                .lastname("Ротенберг")
                .password(new BCryptPasswordEncoder().encode("a"))
                .email("kasha@mail.ru")
                .activationCode(null)
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());

        userRepo.save(User.builder()
                .username("tony64")
                .firstname("Антон")
                .lastname("Васильев")
                .password(new BCryptPasswordEncoder().encode("a"))
                .email("antony@yandex.ru")
                .activationCode(null)
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());

        userRepo.save(User.builder()
                .username("vasiliy228")
                .firstname("Василий")
                .lastname("Самойлов")
                .password(new BCryptPasswordEncoder().encode("a"))
                .email("vasdas@rambler.ru")
                .activationCode(null)
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());

        userRepo.save(User.builder()
                .username("petro123")
                .firstname("Петр")
                .lastname("Добронравов")
                .password(new BCryptPasswordEncoder().encode("a"))
                .email("dobro@mail.ru")
                .activationCode(null)
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());
    }

    public void formDialogsInDataBase() {
        User user1 = userRepo.findByUsername("vdk64").orElse(null);
        User user2 = userRepo.findByUsername("kasha111").orElse(null);
        User user3 = userRepo.findByUsername("petro123").orElse(null);
        Dialog dialog1 = new Dialog(null, Stream.of(user1, user2).collect(Collectors.toSet()),
                new ArrayList<>());
        Dialog dialog2 = new Dialog(null, Stream.of(user1, user3).collect(Collectors.toSet()),
                new ArrayList<>());
        dialogRepo.save(dialog1);
        dialogRepo.save(dialog2);
    }
}
