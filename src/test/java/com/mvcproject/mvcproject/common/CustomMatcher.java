package com.mvcproject.mvcproject.common;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.containsString;

public class CustomMatcher {

    public static Matcher<String> doesNotContainString(String s) {
        return CoreMatchers.not(containsString(s));
    }
}
