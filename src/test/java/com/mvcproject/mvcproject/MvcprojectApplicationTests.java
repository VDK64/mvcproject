package com.mvcproject.mvcproject;

import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.ShowStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.MessageRepo;
import com.mvcproject.mvcproject.repositories.ShowStatusRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MvcprojectApplicationTests {
    @Autowired
    private ShowStatusRepo showStatusRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private DialogRepo dialogRepo;

    @Test
    @Transactional
    public void contextLoads1() {
        User vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        User vasiliy228 = userRepo.findByUsername("vasiliy228").orElseThrow();
        Dialog dialog1 = dialogRepo.findById(1L).orElseThrow();
        ShowStatus showStatus1 = showStatusRepo.findById(2L).orElseThrow();
        System.out.println();
    }
}
