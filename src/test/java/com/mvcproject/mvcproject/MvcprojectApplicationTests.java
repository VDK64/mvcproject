package com.mvcproject.mvcproject;

import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.repositories.DialogRepo;
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
    private DialogRepo dialogRepo;


    @Test
    @Transactional
    public void contextLoads1() {
        List<Dialog> dialogs = dialogRepo.findDialogByContainingUserNative(1L);
        Assert.assertEquals(3, dialogs.size());
    }
}
