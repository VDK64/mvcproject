package com.mvcproject.mvcproject.session;

import com.mvcproject.mvcproject.repositories.OnlineUserRepo;
import com.mvcproject.mvcproject.services.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

@AllArgsConstructor
@Data
public class LoggedUser implements HttpSessionBindingListener {
    private String username;
    private OnlineUserRepo onlineUserRepo;
    private UserService userService;

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        userService.createSessionInfo(((LoggedUser) event.getValue()).getUsername());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        userService.deleteSessionInfo(((LoggedUser) event.getValue()).getUsername());
    }
}
