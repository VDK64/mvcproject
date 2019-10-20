package com.mvcproject.mvcproject;

import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MvcprojectApplicationTests {
    @Autowired
    private UserRepo userRepo;

    @Test
    public void contextLoads() {

    }

}
