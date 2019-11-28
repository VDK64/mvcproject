package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import freemarker.template.utility.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class SettingsService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private Validator validator;
    @Value("${upload.path}")
    private String uploadPath;

    public void saveFile(MultipartFile file, User user) throws IOException {
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
            File uploadDir = new File(uploadPath + "/" + user.getId());
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + user.getId() + "/" + resultFilename));
            user.setAvatar(resultFilename);
            userRepo.save(user);
        }
    }

    public void deleteAvatar(User user, ModelAndView model) {
        if (StringUtil.emptyToNull(user.getAvatar()) == null) {
            throw new CustomServerException(ServerErrors.DEFAULT_AVATAR, model);
        }
        new File(uploadPath + "/" + user.getId(), user.getAvatar()).delete();
        user.setAvatar("default");
        userRepo.save(user);
    }

    public User setSettings(User user, String firstname, String lastname, String username, ModelAndView modelAndView) {
        User userFromDB = userRepo.findById(user.getId()).orElseThrow();
        UserService.ifAdmin(modelAndView, user);
        modelAndView.addObject("user", user);
        if (!(StringUtil.emptyToNull(firstname) == null)) {
            validator.validFirstname(firstname, modelAndView);
            userFromDB.setFirstname(firstname);

        }
        if (!(StringUtil.emptyToNull(lastname) == null)) {
            validator.validLastname(lastname, modelAndView);
            userFromDB.setLastname(lastname);
        }
        if (!(StringUtil.emptyToNull(username) == null)) {
            validator.validUsername(username, modelAndView);
            userFromDB.setUsername(username);
        }
        userRepo.save(userFromDB);
        return userFromDB;
    }

    public void deposit(User user, String deposit, ModelAndView modelAndView) {
        Float value = validator.validValueAndConvertToFlat(deposit, modelAndView);
        if (user.getDeposit() != 0f) { user.setDeposit(user.getDeposit() + value); }
        else { user.setDeposit(value); }
        userRepo.save(user);
    }

    public void withdraw(User user, String deposit, ModelAndView modelAndView) {
        Float value = validator.validValueAndConvertToFlat(deposit, modelAndView);
        if (user.getDeposit() < value) { throw new CustomServerException(ServerErrors.NOT_ENOUGH_DEPOSIT, modelAndView); }
        else { user.setDeposit(user.getDeposit() - value); }
        userRepo.save(user);
    }
}
