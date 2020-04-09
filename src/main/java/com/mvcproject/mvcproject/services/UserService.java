package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.email.EmailService;
import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.session.LoggedUser;
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

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
    @Value("${production}")
    private boolean production;
    @Autowired
    private Validator validator;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private void checkUserExist(User user, ModelAndView model) {
        userRepo.findByUsername(user.getUsername()).
                ifPresent(user1 -> {
                    if (!user1.getId().equals(user.getId()))
                        throw new CustomServerException(ServerErrors.ALREADY_EXIST, model);
                });
    }

    public void createUser(String firstname, String lastname, String username, String password, String email,
                           ModelAndView model, User principal) {
        validator.validate(firstname, lastname, username, password, email, model, principal);
        User user = (User.builder()
                .username(username)
                .firstname(firstname)
                .lastname(lastname)
                .password(passwordEncoder.encode(password))
                .email(email)
                .activationCode(UUID.randomUUID().toString())
                .authorities(Collections.singleton(Role.USER))
                .accountNonExpired(true)
                .avatar("default")
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(false)
                .isOnline(false)
                .deposit(0f)
                .steamId(null)
                .haveNewBets(false)
                .haveNewMessages(false)
                .build());
        if (!production) {
            user.setActivationCode(null);
            user.setEnabled(true);
        }
        checkUserExist(user, model);
        userRepo.save(user);
        new Thread(() -> sendMail(user)).start();
    }

    public static void ifAdmin(Model model, User user) {
        model.addAttribute("admin", user.getAuthorities().contains(Role.ADMIN));
    }

    public static void ifAdmin(ModelAndView model, User user) {
        model.addObject("admin", user.getAuthorities().contains(Role.ADMIN));
    }

    private void sendMail(User user) {
        if (production)
            emailService.sendSimpleMessage(user.getEmail(), subject, String.format(msg, user.getUsername(),
                    user.getActivationCode()));
    }

    public void changeUser(User user, String firstname, String lastname, String password,
                           Map<String, String> authorities, ModelAndView model, User principal) {
        model.addObject("user", principal);
        UserService.ifAdmin(model, principal);
        model.addObject("find", user);
        model.addObject("authorities", Role.values());
        Map<String, String> map = checkUserField(model, firstname, lastname, password, user, principal);
        Set<Role> roles = new LinkedHashSet<>();
        authorities.forEach((s1, s2) -> {
            if (s1.contains("authority")) {
                roles.add(Role.valueOf(s2));
            }
        });
        user.setFirstname(map.get("firstname"));
        user.setLastname(map.get("lastname"));
        checkUserExist(user, model);
        user.setPassword(map.get("password"));
        user.setAuthorities(roles);
        userRepo.save(user);
    }

    private Map<String, String> checkUserField(ModelAndView model, String firstname, String lastname,
                                               String password, User user, User principal) {
        Map<String, String> res = new LinkedHashMap<>();
        if (firstname == null || firstname.equals("")) {
            res.put("firstname", user.getFirstname());
        } else {
            validator.validFirstname(firstname, model, principal);
            res.put("firstname", firstname);
        }
        if (lastname == null || lastname.equals("")) {
            res.put("lastname", user.getLastname());
        } else {
            validator.validLastname(lastname, model, principal);
            res.put("lastname", lastname);
        }
        if (password == null || password.equals("")) {
            res.put("password", user.getPassword());
        } else {
            validator.validPassword(password, model, principal);
            res.put("password", passwordEncoder.encode(password));
        }
        return res;
    }

    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        return userRepo.findByUsername(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("user " + username + " was not found!");
        });
    }

    public boolean confirmEmail(String code, Model model) {
        User user = userRepo.findByActivationCode(code).orElse(null);
        if (user != null && user.getActivationCode().equals(code)) {
            user.setActivationCode(null);
            user.setEnabled(true);
            userRepo.save(user);
            return true;
        } else
            return false;
    }

    @Transactional
    public Map<String, Object> getFriendsSeparately(Long id) {
        Map<String, Object> result = new HashMap<>();
        List<User> friends = new ArrayList<>();
        List<User> unconfirmeds = new ArrayList<>();
        User userFromDB = userRepo.findById(id).orElseThrow();
        invites(userFromDB, result);
        result.put("user", userFromDB);
        userFromDB.getFriends().forEach(user -> {
            if (user.getFriends().contains(userFromDB))
                friends.add(user);
            else
                unconfirmeds.add(user);
        });
        result.put("friends", friends);
        result.put("unconfirmeds", unconfirmeds);
        return result;
    }

    private void invites(User principal, Map<String, Object> result) {
        List<User> whomFriend = userRepo.whomFriend(principal.getId());
        Set<User> invites = whomFriend.stream()
                .filter(user -> !principal.getFriends().contains(user))
                .collect(Collectors.toSet());
        result.put("invites", invites);
    }

    @Transactional
    public Map<String, Object> getFriendsAll(Long id) {
        User userFromDB = userRepo.findById(id).orElseThrow();
        List<User> friends = new ArrayList<>(userFromDB.getFriends());
        return new HashMap<>() {{
            put("user", userFromDB);
            put("friends", friends);
        }};
    }

    public void createSessionInfo(Object user) {
        User user1 = (User) ((LoggedUser) user).getUser();
        user1.setIsOnline(true);
        userRepo.save(user1);
    }

    @Transactional
    public void deleteSessionInfo(Object user) {
        User user1 = (User) ((LoggedUser) user).getUser();
        user1.setIsOnline(false);
        userRepo.save(user1);
    }

    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow();
    }

    public int convertSteamIdTo32(String steamId) {
        return new BigInteger(steamId).subtract(new BigInteger("76561197960265728")).intValue();
    }

    @Transactional
    public User addFriend(Long id, String inviteUsername, ModelAndView model) {
        User userFromDB = userRepo.findById(id).orElseThrow();
        User inviteUser = userRepo.findByUsername(inviteUsername).orElseThrow();
        if (userFromDB.equals(inviteUser)) {
            UserService.ifAdmin(model, userFromDB);
            model.addObject("user", userFromDB);
            model.addObject("newMessages", userFromDB.isHaveNewMessages());
            model.addObject("newBets", userFromDB.isHaveNewBets());
            throw new CustomServerException(ServerErrors.FRIEND_FOR_YOURSELF, model);
        } else if (!userFromDB.getFriends().contains(inviteUser)) {
            userFromDB.getFriends().add(inviteUser);
        } else {
            UserService.ifAdmin(model, userFromDB);
            model.addObject("user", userFromDB);
            model.addObject("newMessages", userFromDB.isHaveNewMessages());
            model.addObject("newBets", userFromDB.isHaveNewBets());
            throw new CustomServerException(ServerErrors.ALREADY_IN_FRIENDS, model);
        }
        return userRepo.save(userFromDB);
    }

    public User findUser(User userFromDB, String username, ModelAndView model) {
        Optional<User> findUser = userRepo.findByUsername(username);
        return findUser.orElseThrow(() -> {
            throw new CustomServerException(ServerErrors.USER_NOT_FOUND, model);
        });
    }

    @Transactional
    public Map<String, Object> isFriend(User principal, long id) {
        User user = getUserById(principal.getId());
        User friend = getUserById(id);
        return new HashMap<>() {{
            put("user", user);
            put("friend", friend);
            put("isFriend", user.getFriends().contains(friend));
        }};
    }
}