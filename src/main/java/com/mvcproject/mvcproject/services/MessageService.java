package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private DialogRepo dialogRepo;
    @Autowired
    private UserRepo userRepo;

    public void getDialogs(User user) {

    }
}
