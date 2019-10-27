package com.mvcproject.mvcproject;

import com.mvcproject.mvcproject.email.EmailService;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MvcprojectApplicationTests {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailService emailService;
    private String request = "";

    @Test
    public void contextLoads() {
        User user = userRepo.findByUsername("user").orElse(null);
        String str = String.format("Hello, %s! To confirm your email, please, follow this link: " +
                        "http://localhost:8090/email/activate", user.getUsername(), user.getActivationCode());
        emailService.sendSimpleMessage("dkvoznyuk@yandex.ru", "FriendBets", str);
    }

}
