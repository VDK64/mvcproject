package com.mvcproject.mvcproject.validation;

import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.GameStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.CustomServerException;
import com.mvcproject.mvcproject.exceptions.InternalServerExceptions;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.BetRepo;
import freemarker.template.utility.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Component
public class Validator {
    @Autowired
    private BetRepo betRepo;
    private final String regexName = "^(?!\\s*$)[а-яА-Яa-zA-z]*$";
    private final String regexUsername = "^[a-zA-Z0-9._-]{3,}$";
    private final String regexEmail = "^(.+)@(.+)$";
    private final String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[-_!@#$%^&*+=])(?=\\S+$).{4,}$";
    private final int maxNameLength = 15;
    private final int minNameLength = 2;
    private final int maxPasswordLength = 15;
    private final int minPasswordLength = 4;

    public void validate(String firstname, String lastname, String username, String password, String email,
                         ModelAndView model) {
        checkFirstname(firstname, model);
        model.addObject("firstname", firstname);
        checkLastname(lastname, model);
        model.addObject("lastname", lastname);
        checkUsername(username, model);
        model.addObject("username", username);
        checkPassword(password, model);
        model.addObject("password", password);
        checkEmail(email, model);
        model.addObject("email", email);
    }

    public void validFirstname(String firstname, ModelAndView modelAndView) {
        checkFirstname(firstname, modelAndView);
        modelAndView.addObject("firstname", firstname);
    }
    public void validLastname(String lastname, ModelAndView modelAndView) {
        checkLastname(lastname, modelAndView);
        modelAndView.addObject("lastname", lastname);
    }

    public void validUsername(String username, ModelAndView modelAndView) {
        checkLastname(username, modelAndView);
        modelAndView.addObject("lastname", username);
    }

    private void checkFirstname(String firstname, ModelAndView model) {
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
        if (firstname.length()<minNameLength) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_FIRSTNAME, minNameLength, maxNameLength),
                    model);
        }
    }

    public Float validateDataToCreateBetAndGame(User user, String value, ModelAndView modelAndView,
                                                User opponentFromDB, String lobbyName, String password) {
        if (StringUtil.emptyToNull(lobbyName) == null) {
            throw new CustomServerException(ServerErrors.LOBBYNAME_NULL, modelAndView);
        }
        if (StringUtil.emptyToNull(password) == null) {
            throw new CustomServerException(ServerErrors.LOBBYPASSWORD_NULL, modelAndView);
        }
        betRepo.findByUserAndOpponentAndWhoWin(user, opponentFromDB, null).ifPresent(bet -> {
            throw new CustomServerException(ServerErrors.BET_EXIST, modelAndView);
        });
        betRepo.findByUserAndOpponentAndWhoWin(opponentFromDB, user, null).ifPresent(bet -> {
            throw new CustomServerException(ServerErrors.BET_EXIST, modelAndView);
        });
        Float floatValue = validValueAndConvertToFlat(value, modelAndView, user);
        if (floatValue > user.getDeposit() || floatValue > opponentFromDB.getDeposit()) {
            throw new CustomServerException(ServerErrors.WRONG_BET_VALUE, modelAndView);
        }
        if (floatValue == 0f) {
            throw new CustomServerException(ServerErrors.WRONG_VALUE, modelAndView);
        }
        return floatValue;
    }

    private void checkLastname(String lastname, ModelAndView model) {
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
        if (lastname.length()<minNameLength) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_FIRSTNAME, minNameLength, maxNameLength),
                    model);
        }
    }

    private void checkUsername(String username, ModelAndView model) {
        if (StringUtil.emptyToNull(username) == null) {
            throw new CustomServerException(ServerErrors.USERNAME_NULL,
                    model);
        }
        if (username.length() > maxNameLength) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_USERNAME, minNameLength, maxNameLength),
                    model);
        }
        if (!username.matches(regexUsername)) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_USERNAME, minNameLength, maxNameLength),
                    model);
        }
    }

    private void checkPassword(String password, ModelAndView model) {
        if (StringUtil.emptyToNull(password) == null) {
            throw new CustomServerException(ServerErrors.PASSWORD_NULL,
                    model);
        }
        if (password.length() > maxNameLength) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_PASSWORD, minPasswordLength,
                    maxPasswordLength), model);
        }
        if (!password.matches(regexPassword)) {
            throw new CustomServerException(String.format(ServerErrors.WRONG_PASSWORD, minPasswordLength,
                    maxPasswordLength), model);
        }
    }

    private void checkEmail(String email, ModelAndView model) {
        if (StringUtil.emptyToNull(email) == null) {
            throw new CustomServerException(ServerErrors.EMAIL_NULL,
                    model);
        }
        if (email.length() < minPasswordLength) {
            throw new CustomServerException(ServerErrors.WRONG_EMAIL, model);
        }
        if (!email.matches(regexEmail)) {
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

    public void validateAndSetDepositAfterBetTaking(User opponentFromDB, Float value, ModelAndView modelAndView) {
        if (opponentFromDB.getDeposit() < value)
            throw new CustomServerException(ServerErrors.NOT_ENOUGH_MONEY, modelAndView);
    }
}
