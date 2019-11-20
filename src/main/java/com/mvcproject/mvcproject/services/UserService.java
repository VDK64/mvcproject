package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.data.DataBaseCreate;
import com.mvcproject.mvcproject.email.EmailService;
import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailService emailService;
    @Value("${project.name}")
    private String subject;
    @Value("${email.str}")
    private String msg;
    @Autowired
    private DataBaseCreate dbCreate;
    @Autowired
    private Validator validator;

    @PostConstruct
    public void init() {
        dbCreate.formUsersInDataBase();
        dbCreate.formDialogsInDataBase();
    }

    private void checkUserExsist(User user, ModelAndView model) {
        userRepo.findByUsername(user.getUsername()).
                ifPresent( user1 -> {
                    if (!user1.getId().equals(user.getId()))
                    throw new CustomServerException(ServerErrors.ALREADY_EXIST, model);
                });
    }

    public void createUser(String firstname, String lastname, String username, String password, String email,
                           ModelAndView model) {
        validator.validate(firstname, lastname, username, password, email, model);
        User user = (User.builder()
                .username(username)
                .firstname(firstname)
                .lastname(lastname)
                .password(new BCryptPasswordEncoder().encode(password))
                .email(email)
                .activationCode(UUID.randomUUID().toString())
                .authorities(Collections.singleton(Role.USER))
                .accountNonExpired(true)
                .avatar("default")
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build());
        checkUserExsist(user, model);
        userRepo.save(user);
        sendMail(user);
    }

    public static void ifAdmin(Model model, User user) {
        if (user.getAuthorities().contains(Role.ADMIN)) { model.addAttribute("admin", true); }
        else { model.addAttribute("admin", false); }
    }

    public static void ifAdmin(ModelAndView model, User user) {
        if (user.getAuthorities().contains(Role.ADMIN)) { model.addObject("admin", true); }
        else { model.addObject("admin", false); }
    }

    private void sendMail(User user) {
        emailService.sendSimpleMessage("dkvoznyuk@yandex.ru", subject, String.format(msg, user.getUsername(),
                user.getActivationCode()));
    }

    public void changeUser(User user, String firstname, String lastname, String username, String password,
                           Map<String, String> authorities, ModelAndView model) {
        Map<String, String> map = checkUserField(firstname, lastname, username, password, user);
        model.addObject("username", user.getUsername());
        model.addObject("authorities", Role.values());
        Set<Role> roles = new LinkedHashSet<>();
        authorities.forEach((s1, s2) -> {
            if (s1.contains("authority")) { roles.add(Role.valueOf(s2)); }
        });
        user.setFirstname(map.get("firstname"));
        user.setLastname(map.get("lastname"));
        user.setUsername(map.get("username"));
        checkUserExsist(user, model);
        user.setPassword(map.get("password"));
        user.setAuthorities(roles);
        userRepo.save(user);
    }

    private Map<String, String> checkUserField(String firstname, String lastname, String username, String password, User user) {
        Map<String, String> res = new LinkedHashMap<>();
        if (firstname == null || firstname.equals("")) {
            res.put("firstname", user.getFirstname());
        } else { res.put("firstname", firstname); }
        if (lastname == null || lastname.equals("")) {
            res.put("lastname", user.getLastname());
        } else { res.put("lastname", lastname); }
        if (username == null || username.equals("")) {
            res.put("username", user.getUsername());
        } else { res.put("username", username); }
        if (password == null || password.equals("")) {
            res.put("password", user.getPassword());
        } else { res.put("password", new BCryptPasswordEncoder().encode(password)); }
        return res;
    }

    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        return userRepo.findByUsername(username).orElseThrow( () -> {
            throw new UsernameNotFoundException("user " + username + " was not found!");} );
    }

    public boolean confirmEmail(String code, Model model) {
        User user = userRepo.findByActivationCode(code).orElse(null);
        if (user != null && user.getActivationCode().equals(code)) {
            model.addAttribute("username", user.getUsername());
            if (user.getAuthorities().contains(Role.ADMIN)) {
                model.addAttribute("admin", "true");
            }
            user.setActivationCode(null);
            userRepo.save(user);
            return true;
        } else {
            model.addAttribute("username", "unknown");
            return false;
        }
    }
}