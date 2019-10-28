package com.mvcproject.mvcproject.validation;

import freemarker.template.utility.StringUtil;
import org.springframework.stereotype.Component;

@Component
public class Validator {
    private final String regexName = "^(?!\\s*$)[а-яА-Яa-zA-z]*$";
    private final String regexUsername = "^[a-zA-Z0-9._-]{3,}$";
    private final String regexEmail = "^(.+)@(.+)$";
    private final int maxNameLength= 15;
    private final int minNameLength = 1;
    private final int maxPasswordLength = 15;
    private final int minPasswordLength = 4;

    public void checkFirstname(String firstname) {
        if (StringUtil.emptyToNull(firstname) == null) { }
    }


}
