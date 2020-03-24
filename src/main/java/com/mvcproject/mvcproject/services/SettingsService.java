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
import java.math.BigDecimal;
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
                //noinspection ResultOfMethodCallIgnored
                uploadDir.mkdirs();
            }
            if (user.getAvatar().equals("default")) {
                String uuidFile = UUID.randomUUID().toString();
                String resultFilename = uuidFile + "." + file.getOriginalFilename();
                file.transferTo(new File(uploadPath + "/" + user.getId() + "/" + resultFilename));
                user.setAvatar(resultFilename);
                userRepo.save(user);
            }
        }
    }

    public void deleteAvatar(User user, ModelAndView model) {
        if (StringUtil.emptyToNull(user.getAvatar()) == null) {
            model.addObject("user", user);
            throw new CustomServerException(ServerErrors.DEFAULT_AVATAR, model);
        }
        //noinspection ResultOfMethodCallIgnored
        new File(uploadPath + "/" + user.getId(), user.getAvatar()).delete();
        //noinspection ResultOfMethodCallIgnored
        new File(uploadPath + "/" + user.getId()).delete();
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
        Float value = validator.validValueAndConvertToFlat(deposit, modelAndView, user);
        BigDecimal val = new BigDecimal(value);
        BigDecimal usrValue = BigDecimal.valueOf(user.getDeposit());
        BigDecimal result = usrValue.add(val);
        user.setDeposit(result.floatValue());
        userRepo.save(user);
    }

    public void withdraw(User user, String deposit, ModelAndView modelAndView) {
        Float value = validator.validValueAndConvertToFlat(deposit, modelAndView, user);
        BigDecimal val = new BigDecimal(value);
        BigDecimal usrValue = BigDecimal.valueOf(user.getDeposit());
        BigDecimal result = usrValue.subtract(val);
        if (user.getDeposit() < value) {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.NOT_ENOUGH_DEPOSIT_TO_TRANSACTION, modelAndView);
        } else {
            user.setDeposit(result.floatValue());
        }
        userRepo.save(user);
    }

    public void setSteamId(User user, String identity) {
        user.setSteamId(getSteamIdFromIdentity(identity));
        userRepo.save(user);
    }

    private String getSteamIdFromIdentity(String identity) {
        return identity.split("/")[5];
    }
}
