package com.mvcproject.mvcproject;

import com.mvcproject.mvcproject.email.EmailService;
import com.mvcproject.mvcproject.entities.DLGKey;
import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.MessageRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MvcprojectApplicationTests {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DialogRepo dialogRepo;
    @Autowired
    private MessageRepo messageRepo;

    @Test
//    @Transactional
    public void contextLoads() {
        Set<Role> roles = new HashSet<>(){{add(Role.USER);}};
        User user1 = new User(null, "Anton", "Alekseev", "aleks555",
                "mail@mail.ru", null, null, roles, "pass", true,
                true, true, true, new LinkedHashSet<>());
        userRepo.save(user1);
        User user2 = new User(null, "Petr", "Ivanov", "Petr555",
                "mail@rambler.ru", null, null, roles, "pass", true,
                true, true, true, new LinkedHashSet<>());
        userRepo.save(user2);
        user1 = userRepo.findByUsername("aleks555").orElse(null);
        user2 = userRepo.findByUsername("Petr555").orElse(null);
        Dialog dialog1 = new Dialog(new DLGKey(user1.getId(), user2.getId()), user1, new ArrayList<>());
        Dialog dialog2 = new Dialog(new DLGKey(user2.getId(), user1.getId()), user2, new ArrayList<>());
        dialogRepo.save(dialog1);
        dialogRepo.save(dialog2);
        System.out.println();
    }

}
