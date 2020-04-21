package com.mvcproject.mvcproject.validation;

import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.GameStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.InternalServerExceptions;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.BetRepo;
import freemarker.template.utility.StringUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class Validator {
    @Autowired
    private BetRepo betRepo;
    private final String regexName = "^(?!\\s*$)[а-яА-Яa-zA-z]*$";
    @Getter
    private final int maxNameLength = 15;
    @Getter
    private final int minNameLength = 2;
    @Getter
    private final int maxPasswordLength = 15;
    @Getter
    private final int minPasswordLength = 4;

    public void validate(String firstname, String lastname, String username, String password, String email,
                         ModelAndView model, User principal) {
        checkFirstname(firstname, model, principal);
        model.addObject("firstname", firstname);
        checkLastname(lastname, model, principal);
        model.addObject("lastname", lastname);
        checkUsername(username, model, principal);
        model.addObject("username", username);
        checkPassword(password, model, principal);
        model.addObject("password", password);
        checkEmail(email, model, principal);
        model.addObject("email", email);
    }

    public void validFirstname(String firstname, ModelAndView modelAndView, User principal) {
        checkFirstname(firstname, modelAndView, principal);
        modelAndView.addObject("firstname", firstname);
    }
    public void validLastname(String lastname, ModelAndView modelAndView, User principal) {
        checkLastname(lastname, modelAndView, principal);
        modelAndView.addObject("lastname", lastname);
    }

    public void validUsername(String username, ModelAndView modelAndView, User principal) {
        checkUsername(username, modelAndView, principal);
        modelAndView.addObject("lastname", username);
    }

    public void validPassword(String password, ModelAndView modelAndView, User principal) {
        checkPassword(password, modelAndView, principal);
        modelAndView.addObject("password", password);
    }

    private void formUserData(User principal, ModelAndView modelAndView) {
        if (principal != null) {
            modelAndView.addObject("newMessages", principal.isHaveNewMessages());
            modelAndView.addObject("newBets", principal.isHaveNewBets());
        }
    }

    private void checkFirstname(String firstname, ModelAndView model, User principal) {
        if (StringUtil.emptyToNull(firstname) == null) {
            formUserData(principal, model);
            throw new CustomServerException(ServerErrors.FIRSTNAME_NULL,
                    model);
        }
        if (firstname.length() > maxNameLength) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_FIRSTNAME, minNameLength, maxNameLength),
                    model);
        }
        if (!firstname.matches(regexName)) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_FIRSTNAME, minNameLength, maxNameLength),
                    model);
        }
        if (firstname.length()<minNameLength) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_FIRSTNAME, minNameLength, maxNameLength),
                    model);
        }
    }

    public Float validateDataToCreateBetAndGame(User user, String value, ModelAndView modelAndView,
                                                User opponentFromDB, String lobbyName, String password) {
        if (StringUtil.emptyToNull(lobbyName) == null) {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.LOBBYNAME_NULL, modelAndView);
        }
        if (StringUtil.emptyToNull(password) == null) {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.LOBBYPASSWORD_NULL, modelAndView);
        }
        betRepo.findByUserAndOpponentAndWhoWin(user, opponentFromDB, null).ifPresent(bet -> {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.BET_EXIST, modelAndView);
        });
        betRepo.findByUserAndOpponentAndWhoWin(opponentFromDB, user, null).ifPresent(bet -> {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.BET_EXIST, modelAndView);
        });
        Float floatValue = validValueAndConvertToFlat(value, modelAndView, user);
        if (floatValue > user.getDeposit() || floatValue > opponentFromDB.getDeposit()) {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.WRONG_BET_VALUE, modelAndView);
        }
        if (floatValue == 0f) {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.WRONG_VALUE, modelAndView);
        }
        return floatValue;
    }

    private void checkLastname(String lastname, ModelAndView model, User principal) {
        if (StringUtil.emptyToNull(lastname) == null) {
            formUserData(principal, model);
            throw new CustomServerException(ServerErrors.LASTNAME_NULL,
                    model);
        }
        if (lastname.length() > maxNameLength) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_LASTNAME, minNameLength, maxNameLength),
                    model);
        }
        if (!lastname.matches(regexName)) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_LASTNAME, minNameLength, maxNameLength),
                    model);
        }
        if (lastname.length()<minNameLength) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_LASTNAME, minNameLength, maxNameLength),
                    model);
        }
    }

    private void checkUsername(String username, ModelAndView model, User principal) {
        if (StringUtil.emptyToNull(username) == null) {
            formUserData(principal, model);
            throw new CustomServerException(ServerErrors.USERNAME_NULL,
                    model);
        }
        if (username.length() > maxNameLength) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_USERNAME, minNameLength, maxNameLength),
                    model);
        }
        if (username.length() < minNameLength) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_USERNAME, minNameLength, maxNameLength),
                    model);
        }
        if (!username.matches("^[a-zA-Z0-9._-]{3,}$")) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_USERNAME, minNameLength, maxNameLength),
                    model);
        }
    }

    private void checkPassword(String password, ModelAndView model, User principal) {
        if (StringUtil.emptyToNull(password) == null) {
            formUserData(principal, model);
            throw new CustomServerException(ServerErrors.PASSWORD_NULL,
                    model);
        }
        if (password.length() > maxNameLength) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_PASSWORD, minPasswordLength,
                    maxPasswordLength), model);
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-_!@#$%^&*+=])(?=\\S+$).{4,}$")) {
            formUserData(principal, model);
            throw new CustomServerException(String.format(ServerErrors.WRONG_PASSWORD, minPasswordLength,
                    maxPasswordLength), model);
        }
    }

    private void checkEmail(String email, ModelAndView model, User principal) {
        if (StringUtil.emptyToNull(email) == null) {
            formUserData(principal, model);
            throw new CustomServerException(ServerErrors.EMAIL_NULL,
                    model);
        }
        if (email.length() < minPasswordLength) {
            formUserData(principal, model);
            throw new CustomServerException(ServerErrors.WRONG_EMAIL, model);
        }
        if (!email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")) {
            formUserData(principal, model);
            throw new CustomServerException(ServerErrors.WRONG_EMAIL, model);
        }
    }

    private Float checkCorrectlyValueOfDeposit(String deposit, ModelAndView modelAndView, User user) {
        String replace = deposit.replace(',', '.');
        @SuppressWarnings("WrapperTypeMayBePrimitive") Float value;
        try {
            value = ((float) Math.floor(Float.parseFloat(replace) * 100)) / 100;
        } catch (NumberFormatException e) {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.WRONG_VALUE, modelAndView);
        }
        if (value<0) {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.WRONG_VALUE, modelAndView);
        }
        return value;
    }

    public Float validValueAndConvertToFlat(String deposit, ModelAndView modelAndView, User user) {
        return checkCorrectlyValueOfDeposit(deposit, modelAndView, user);
    }

    public void validateStatus(Game game, GameStatus gameStatus) {
        if ((game.getStatus() == null && (gameStatus == GameStatus.LEAVE || gameStatus == GameStatus.TIMEOUT)) ||
        ((game.getStatus() == GameStatus.LEAVE || game.getStatus() == GameStatus.TIMEOUT)
                && gameStatus == GameStatus.POSITIVE_LEAVE)) {
            throw new InternalServerExceptions(ServerErrors.WRONG_CHANGE_STATUS);
        }
    }

    public void validateToSetConfirm(User user, Float value, ModelAndView modelAndView) {
        if (StringUtil.emptyToNull(user.getSteamId()) == null) {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.STEAM_ID_NULL, modelAndView);
        }
        if (user.getDeposit() < value) {
            modelAndView.addObject("user", user);
            throw new CustomServerException(ServerErrors.NOT_ENOUGH_MONEY, modelAndView);
        }
    }
}
