package com.mvcproject.mvcproject.config;

import com.mvcproject.mvcproject.exceptions.MyCustomFailureHandler;
import com.mvcproject.mvcproject.services.UserService;
import com.mvcproject.mvcproject.session.MyLogoutSuccessHandler;
import com.mvcproject.mvcproject.session.MySimpleUrlAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;
    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;
    @Autowired
    private MySimpleUrlAuthenticationSuccessHandler mySimpleUrlAuthenticationSuccessHandler;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MyCustomFailureHandler myCustomFailureHandler;

    //Handlers are disabled because app working with dev tools and save last sessions.

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .authorizeRequests()
                    .antMatchers("/readme.txt", "/css/*", "/register", "/login?ok", "/email/**",
                            "/static/**", "/favicon.ico", "/dota2/**", "/h2-console/**", "/login/**").permitAll()
                .anyRequest().authenticated()
                .and()
                    .formLogin().loginPage("/login").permitAll()
                .and()
                    .formLogin().failureHandler(myCustomFailureHandler)
//                    .successHandler(mySimpleUrlAuthenticationSuccessHandler)
                .and()
                    .logout()
//                    .logoutSuccessHandler(myLogoutSuccessHandler)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
                .and()
                    .logout()
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                .and()
                    .csrf()
                    .ignoringAntMatchers("/room/**")
                    .ignoringAntMatchers("/dota2/**")
                    .ignoringAntMatchers("/newMessage/**")
                    .ignoringAntMatchers("/bet")
                .and()
                    .headers().frameOptions().sameOrigin();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }
}