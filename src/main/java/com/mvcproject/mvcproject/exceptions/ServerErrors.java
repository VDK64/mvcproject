package com.mvcproject.mvcproject.exceptions;

public class ServerErrors {
    public static final String ALREADY_EXIST = "This username is already exist. Please, try another username.";
    public static final String FIRSTNAME_NULL = "First name can not be empty!";
    public static final String WRONG_FIRSTNAME = "Wrong first name! Use only letters. First name have a range %d - %d";
    public static final String WRONG_LASTNAME = "Wrong last name! Use only letters. Last name have a range %d - %d";
    public static final String LASTNAME_NULL = "Last name can not be empty!";
    public static final String WRONG_PASSWORD = "Wrong password! Password must consist from letters" +
            "(one upper case), min. one symbol (-_!@#$^&*+=) and min. one number. Password range %d - %d";
    public static final String PASSWORD_NULL = "Password can not be null!";
    public static final String WRONG_EMAIL = "Wrong email. Please write an actual email.";
    public static final String EMAIL_NULL = "Email can not be null.";
    public static final String FILE_LIMIT = "File size exceeds limit!";
    public static final String USERNAME_NULL = "Username can not be null";
    public static final String WRONG_USERNAME = "Username can consist from letters, numbers, '-', '_', '.', and have" +
            "a range %d - %d ";
    public static final String DEFAULT_AVATAR = "This is default avatar. You can not delete it!";
    public static final String NOT_ENOUGH_DEPOSIT_TO_TRANSACTION = "Sorry, but your deposit is lower than the transaction value!";
    public static final String WRONG_VALUE = "Please, write a correct value!";
    public static final String WRONG_QUERY = "Wrong query!";
    public static final String WRONG_BET_VALUE = "Please, choose lower bet value.";
    public static final String BET_EXIST = "This bet is already exist.";
    public static final String LOBBYNAME_NULL = "Lobby name can not be null or empty";
    public static final String LOBBYPASSWORD_NULL = "Lobby password can not be null";
    public static final String WRONG_CHANGE_STATUS = "Wrong change status in lobby. Bot api is out of sync";
    public static final String NOT_ENOUGH_MONEY = "Sorry, but your deposit is lower than the bet value!";
    public static final String USER_NOT_FOUND = "Sorry, user not found!";
    public static final String ALREADY_IN_FRIENDS = "This user is already your friend!";
}
