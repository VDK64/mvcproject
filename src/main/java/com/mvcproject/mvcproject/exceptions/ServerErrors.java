package com.mvcproject.mvcproject.exceptions;

public class ServerErrors {
    public static final String ALREADY_EXIST = "This username is already exist. Please, try another username.";
    public static final String FIRSTNAME_NULL = "First name can not be empty!";
    public static final String WRONG_FIRSTNAME = "Wrong first name! Use only letters. First name have a range %d - %d";
    public static final String WRONG_LASTNAME = "Wrong last name! Use only letters. Last name have a range %d - %d";
    public static final String LASTNAME_NULL = "Last name can not be empty!";
    public static final String WRONG_PASSWORD = "Wrong password!";
    public static final String WRONG_EMAIL = "Wrong email. Please write an actual email.";
    public static final String EMAIL_NULL = "Email can not be null.";
    public static final String FILE_LIMIT = "File size exceeds limit!";
    public static final String USERNAME_NULL = "Username can not be null";
    public static final String WRONG_USERNAME = "Username can consist from letters, numbers, '-', '_', '.', and have" +
            "a range %d - %d ";
}
