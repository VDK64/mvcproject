package com.mvcproject.mvcproject;

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
    private String request = "";

    @Test
    public void contextLoads() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON_UTF8));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        BigInteger modulus = new BigInteger("F56D...", 16);
        BigInteger pubExp = new BigInteger("010001", 16);
    }

}
