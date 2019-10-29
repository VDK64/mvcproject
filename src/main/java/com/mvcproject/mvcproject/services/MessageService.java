package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.dto.UserDtoResponse;
import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class MessageService {
    @Autowired
    private DialogRepo dialogRepo;
    @Autowired
    private UserRepo userRepo;

    @Transactional
    public Set<UserDtoResponse> getDialogs(Long id) {
        Set<UserDtoResponse> response = new LinkedHashSet<>();
        User user = userRepo.findById(id).orElse(null);
        assert user != null;
        Set<Dialog> dialogs = user.getDialogs();
        for (Dialog dialog : dialogs) {
            for (User dUser : dialog.getUsers()) {
                if (!dUser.getId().equals(id)) {
                    response.add(new UserDtoResponse(dUser.getFirstname(), dUser.getLastname(), dUser.getUsername()));
                }
            }
        }
        return response;
    }
}
