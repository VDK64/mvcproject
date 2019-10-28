package com.mvcproject.mvcproject.validation;

import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import freemarker.template.utility.StringUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class Validator {
    private final String regexName = "^(?!\\s*$)[а-яА-Яa-zA-z]*$";
    private final String regexUsername = "^[a-zA-Z0-9._-]{3,}$";
    private final String regexEmail = "^(.+)@(.+)$";
    private final String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-_!@#$%^&*+=])(?=\\S+$).{4,}$";
    private final int maxNameLength = 15;
    private final int minNameLength = 1;
    private final int maxPasswordLength = 15;
    private final int minPasswordLength = 4;

    public void checkFirstname(String firstname, ModelAndView model) {
        if (StringUtil.emptyToNull(firstname) == null) {
            throw new CustomServerException(ServerErrors.FIRSTNAME_NULL,
                    model);
        }
        if (firstname.length() > maxNameLength) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_FIRSTNAME, minNameLength, maxNameLength),
                    model);
        }
        if (!firstname.matches(regexName)) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_FIRSTNAME, minNameLength, maxNameLength),
                    model);
        }
    }

    public void checkLastname(String lastname, ModelAndView model) {
        if (StringUtil.emptyToNull(lastname) == null) {
            throw new CustomServerException(ServerErrors.LASTNAME_NULL,
                    model);
        }
        if (lastname.length() > maxNameLength) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_LASTNAME, minNameLength, maxNameLength),
                    model);
        }
        if (!lastname.matches(regexName)) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_LASTNAME, minNameLength, maxNameLength),
                    model);
        }
    }

    public void checkUsername(String username, ModelAndView model) {
        if (StringUtil.emptyToNull(username) == null) {
            throw new CustomServerException(ServerErrors.USERNAME_NULL,
                    model);
        }
        if (username.length() > maxNameLength) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_USERNAME, minNameLength, maxNameLength),
                    model);
        }
        if (!username.matches(regexName)) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_USERNAME, minNameLength, maxNameLength),
                    model);
        }
    }

    public void checkPassword(String password, ModelAndView model) {
        if (StringUtil.emptyToNull(password) == null) {
            throw new CustomServerException(ServerErrors.PASSWORD_NULL,
                    model);
        }
        if (password.length() > maxNameLength) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_USERNAME, minPasswordLength,
                    maxPasswordLength), model);
        }
        if (!password.matches(regexName)) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_USERNAME, minPasswordLength,
                    maxPasswordLength), model);
        }
    }

    public void checkEmail(String email, ModelAndView model) {
        if (StringUtil.emptyToNull(email) == null) {
            throw new CustomServerException(ServerErrors.EMAIL_NULL,
                    model);
        }
        if (email.length() > maxNameLength || email.length() < minPasswordLength) {
            throw new CustomServerException(ServerErrors.WRONG_EMAIL, model);
        }
        if (!email.matches(regexName)) {
            throw new CustomServerException(ServerErrors.WRONG_EMAIL, model);
        }
    }
}
