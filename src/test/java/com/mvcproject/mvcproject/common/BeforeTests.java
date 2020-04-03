package com.mvcproject.mvcproject.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BeforeTests {
    @Autowired
    private DataBaseContent dataBaseContent;

    @PostConstruct
    public void init() {
        dataBaseContent.createUsersInDataBase();
        dataBaseContent.createDialogsInDataBase();
        dataBaseContent.createBetsInDataBase();
        dataBaseContent.formFriends();
    }
}