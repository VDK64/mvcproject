package com.mvcproject.mvcproject.exceptions;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyCustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        saveException(request, exception);
        if (exception instanceof DisabledException)
            getRedirectStrategy().sendRedirect(request, response, "/login?error=disabled");
        else
            getRedirectStrategy().sendRedirect(request, response, "/login?error");
    }
}