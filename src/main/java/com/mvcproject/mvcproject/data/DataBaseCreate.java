package com.mvcproject.mvcproject.data;

import com.mvcproject.mvcproject.entities.*;
import com.mvcproject.mvcproject.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataBaseCreate {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DialogRepo dialogRepo;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private GameRepo gameRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void createUsersInDataBase() {
        userRepo.save(User.builder()
                .username("vdk64")
                .firstname("Дмитрий")
                .lastname("Вознюк")
                .password(passwordEncoder.encode("a"))
                .email("dkvoznyuk@yandex.ru")
                .activationCode(null)
                .avatar("default")
                .authorities(Stream.of(Role.USER, Role.ADMIN).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .deposit(100f)
                .isOnline(false)
                .steamId("76561198799034987")
                .haveNewBets(false)
                .haveNewMessages(false)
                .build());

        userRepo.save(User.builder()
                .username("user")
                .firstname("Ivan")
                .lastname("Petrov")
                .password(passwordEncoder.encode("a"))
                .email("user@mail.ru")
                .activationCode(null)
                .avatar("default")
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .deposit(100f)
                .isOnline(false)
                .haveNewBets(false)
                .haveNewMessages(false)
                .build());

        userRepo.save(User.builder()
                .username("kasha111")
                .firstname("Аркадий")
                .lastname("Ротенберг")
                .password(passwordEncoder.encode("a"))
                .email("kasha@mail.ru")
                .activationCode(null)
                .avatar("default")
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .deposit(100f)
                .isOnline(false)
                .steamId("76561199004382586")
                .haveNewBets(false)
                .haveNewMessages(false)
                .build());

        userRepo.save(User.builder()
                .username("tony64")
                .firstname("Антон")
                .lastname("Васильев")
                .password(passwordEncoder.encode("a"))
                .email("antony@yandex.ru")
                .activationCode(null)
                .avatar("default")
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .deposit(100f)
                .isOnline(false)
                .haveNewBets(false)
                .haveNewMessages(false)
                .build());

        userRepo.save(User.builder()
                .username("vasiliy228")
                .firstname("Василий")
                .lastname("Самойлов")
                .password(passwordEncoder.encode("a"))
                .email("vasdas@rambler.ru")
                .activationCode(null)
                .avatar("default")
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .deposit(100f)
                .isOnline(false)
                .haveNewBets(false)
                .haveNewMessages(false)
                .build());

        userRepo.save(User.builder()
                .username("petro123")
                .firstname("Петр")
                .lastname("Добронравов")
                .password(passwordEncoder.encode("a"))
                .email("dobro@mail.ru")
                .activationCode(null)
                .avatar("default")
                .authorities(Stream.of(Role.USER).collect(Collectors.toSet()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .deposit(100f)
                .isOnline(false)
                .haveNewBets(false)
                .haveNewMessages(false)
                .build());
    }

    public void createDialogsInDataBase() {
        User vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        User kasha111 = userRepo.findByUsername("kasha111").orElseThrow();
        User petro123 = userRepo.findByUsername("petro123").orElseThrow();
        User vasiliy228 = userRepo.findByUsername("vasiliy228").orElseThrow();
        Dialog dialog1 = new Dialog(null, Stream.of(vdk64, kasha111).collect(Collectors.toSet()),
                new ArrayList<>(), false, System.currentTimeMillis());
        Dialog dialog2 = new Dialog(null, Stream.of(vdk64, petro123).collect(Collectors.toSet()),
                new ArrayList<>(), false, System.currentTimeMillis() + 4000L);
        Dialog dialog3 = new Dialog(null, Stream.of(vdk64, vasiliy228).collect(Collectors.toSet()),
                new ArrayList<>(), false, System.currentTimeMillis() + 7000L);
        dialogRepo.save(dialog1);
        dialogRepo.save(dialog2);
        dialogRepo.save(dialog3);
        Message message1 = new Message(null, "Hey, Kasha!", new Date(), vdk64.getId(), kasha111.getId(), dialog1
                , false);
        Message message2 = new Message(null, "Hello, vkd64!", new Date(), kasha111.getId(), vdk64.getId(), dialog1
                , false);
        Message message3 = new Message(null, "How are you?", new Date(), vdk64.getId(), kasha111.getId(), dialog1,
                false);
        Message message4 = new Message(null, "Fine, and you?", new Date(), kasha111.getId(), vdk64.getId(), dialog1,
                false);
        Message message5 = new Message(null, "Fine thanks", new Date(), vdk64.getId(), kasha111.getId(), dialog1,
                false);
        messageRepo.save(message1);
        messageRepo.save(message2);
        messageRepo.save(message3);
        messageRepo.save(message4);
        messageRepo.save(message5);
    }

    public void createBetsInDataBase() {
        User vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        User kasha111 = userRepo.findByUsername("kasha111").orElseThrow();
        User petro123 = userRepo.findByUsername("petro123").orElseThrow();
        User tony64 = userRepo.findByUsername("tony64").orElseThrow();
        User user = userRepo.findByUsername("user").orElseThrow();
        Game game = new Game(null, "MyLobby", "app", "1x1",
                true, true, "76561198799034987", "76561199004382586");
        game.setServerStartTime(1584623845 - 100000);
        game.setStatus(GameStatus.POSITIVE_LEAVE);
        gameRepo.save(game);

        Iterable<Bet> bets = betRepo.saveAll(new ArrayList<>() {{
//            add(new Bet(null, vdk64, 500f, kasha111, false, vdk64.getUsername(), false));
            add(new Bet(null, vdk64, 450f, kasha111, true, null, game, false));
            add(new Bet(null, petro123, 730f, vdk64, false, vdk64.getUsername(), false));
//            add(new Bet(null, vdk64, 150f, tony64, false, null, false));
            add(new Bet(null, user, 200f, vdk64, false, vdk64.getUsername(), false));
        }});
    }
}
