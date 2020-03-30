package com.mvcproject.mvcproject.data;

import com.mvcproject.mvcproject.entities.*;
import com.mvcproject.mvcproject.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
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
    @Autowired
    private ShowStatusRepo showStatusRepo;

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
        vdk64.setHaveNewMessages(false);
        userRepo.save(vdk64);
        Dialog dialog1 = new Dialog(null, Stream.of(vdk64, kasha111).collect(Collectors.toSet()),
                new ArrayList<>(), null,false, System.currentTimeMillis());
        Dialog dialog2 = new Dialog(null, Stream.of(vdk64, petro123).collect(Collectors.toSet()),
                new ArrayList<>(), new ArrayList<>(), false, System.currentTimeMillis() + 4000L);
        Dialog dialog3 = new Dialog(null, Stream.of(vdk64, vasiliy228).collect(Collectors.toSet()),
                new ArrayList<>(), new ArrayList<>(), false, System.currentTimeMillis() + 7000L);
        Dialog saveDialog1 = dialogRepo.save(dialog1);
        Dialog saveDialog2 = dialogRepo.save(dialog2);
        Dialog saveDialog3 = dialogRepo.save(dialog3);
        Message message1 = new Message(null, "Hey, Kasha!", new Date(), vdk64.getId(), kasha111.getId(), saveDialog1
                , false);
        Message message2 = new Message(null, "Hello, vkd64!", new Date(), kasha111.getId(), vdk64.getId(), saveDialog1
                , false);
        Message message3 = new Message(null, "How are you?", new Date(), vdk64.getId(), kasha111.getId(), saveDialog1,
                false);
        Message message4 = new Message(null, "Fine, and you?", new Date(), kasha111.getId(), vdk64.getId(), saveDialog1,
                false);
        Message message5 = new Message(null, "Fine thanks", new Date(), vdk64.getId(), kasha111.getId(), saveDialog1,
                false);
        Message message6 = new Message(null, "vasiliy228 text", new Date(), vasiliy228.getId(), vdk64.getId(), saveDialog3,
                false);
        Message message7 = new Message(null, "petro123 text", new Date(), petro123.getId(), vdk64.getId(), saveDialog2,
                false);
        ShowStatus showStatus1 = new ShowStatus(null, vdk64.getUsername(), saveDialog1, true);
        ShowStatus showStatus2 = new ShowStatus(null, kasha111.getUsername(), saveDialog1, true);
        ShowStatus showStatus3 = new ShowStatus(null, vdk64.getUsername(), saveDialog2, true);
        ShowStatus showStatus4 = new ShowStatus(null, petro123.getUsername(), saveDialog2, true);
        ShowStatus showStatus5 = new ShowStatus(null, vdk64.getUsername(), saveDialog3, true);
        ShowStatus showStatus6 = new ShowStatus(null, vasiliy228.getUsername(), saveDialog3, true);
        showStatusRepo.save(showStatus1);
        showStatusRepo.save(showStatus2);
        showStatusRepo.save(showStatus3);
        showStatusRepo.save(showStatus4);
        showStatusRepo.save(showStatus5);
        showStatusRepo.save(showStatus6);
        messageRepo.save(message1);
        messageRepo.save(message2);
        messageRepo.save(message3);
        messageRepo.save(message4);
        messageRepo.save(message5);
        messageRepo.save(message6);
        messageRepo.save(message7);
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

    @Transactional
    public void addFriends() {
        User vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        User kasha111 = userRepo.findByUsername("kasha111").orElseThrow();
        User petro123 = userRepo.findByUsername("petro123").orElseThrow();
        User tony64 = userRepo.findByUsername("tony64").orElseThrow();
        User vasiliy228 = userRepo.findByUsername("vasiliy228").orElseThrow();
        vdk64.getFriends().add(kasha111);
        vdk64.getFriends().add(petro123);
        vdk64.getFriends().add(vasiliy228);
        vdk64.getFriends().add(tony64);
        kasha111.getFriends().add(vdk64);
        userRepo.save(kasha111);
        userRepo.save(vdk64);
    }
}
