package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
public class MainControllerTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @Autowired
    private Validator validator;
    @Autowired
    private UserRepo userRepo;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private void testEndpointModel(String paramName, String paramValue, Object expectMessage,
                                   MultiValueMap<String, String> params) {
        try {
            mockMvc.perform(post("/register")
                    .with(csrf())
                    .params(params)
                    .param(paramName, paramValue))
                    .andDo(print())
                    .andExpect(model().attributeExists("error"))
                    .andExpect(model().attribute("error", expectMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRegisterUserWithWrongEmail() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("firstname"));
            put("lastname", Collections.singletonList("lastname"));
            put("username", Collections.singletonList("username"));
            put("password", Collections.singletonList("Passworld@12"));
        }};
        List<String> emailList = new ArrayList<>(Arrays.asList("a", "ab", "ab@."));
        emailList.forEach(email -> testEndpointModel("email", email,
                ServerErrors.WRONG_EMAIL, params));
        testEndpointModel("email", "", ServerErrors.EMAIL_NULL, params);
    }

    @Test
    public void testRegisterUserWithWrongUserName() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("firstname"));
            put("lastname", Collections.singletonList("lastname"));
            put("password", Collections.singletonList("Passworld@12"));
            put("email", Collections.singletonList("asd@asd.ru"));
        }};

        List<String> userNameList = new ArrayList<>(Arrays.asList("a", "фы№", "asd@",
                "asdczAsdqweFgtrAasdawq"));
        userNameList.forEach(userName -> testEndpointModel("username", userName,
                String.format(ServerErrors.WRONG_USERNAME, validator.getMinNameLength(),
                        validator.getMaxNameLength()), params));
        List<String> wrightUsernameList = new ArrayList<>(Arrays.asList("alts1993", "a-petrov", "p.alekseev123",
                "asd_vdk64"));
        wrightUsernameList.forEach(userName -> {
            try {
                mockMvc.perform(post("/register")
                        .with(csrf())
                        .params(params)
                        .param("username", userName))
                        .andDo(print())
                        .andExpect(model().attributeDoesNotExist("error"))
                        .andExpect(flash().attribute("ok", "true"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        testEndpointModel("username", "",
                ServerErrors.USERNAME_NULL, params);
    }

    @Test
    public void testRegisterUserWithWrongLastName() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("firstname"));
            put("username", Collections.singletonList("username"));
            put("password", Collections.singletonList("Passworld@12"));
            put("email", Collections.singletonList("asd@asd.ru"));
        }};

        List<String> lastNameList = new ArrayList<>(Arrays.asList("a", "фы№", "asd@", "asd1",
                "asdczAsdqweFgtrAasdawq"));
        lastNameList.forEach(lastName -> testEndpointModel("lastname", lastName,
                String.format(ServerErrors.WRONG_LASTNAME, validator.getMinNameLength(),
                        validator.getMaxNameLength()), params));
        testEndpointModel("lastname", "",
                ServerErrors.LASTNAME_NULL, params);
    }

    @Test
    public void testRegisterUserWithWrongFirstName() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("lastname", Collections.singletonList("lastname"));
            put("username", Collections.singletonList("username"));
            put("password", Collections.singletonList("Passworld@12"));
            put("email", Collections.singletonList("asd@asd.ru"));
        }};
        List<String> firstNameList = new ArrayList<>(Arrays.asList("a", "фы№", "asd@", "asd1",
                "asdczAsdqweFgtrAasdawq"));
        firstNameList.forEach(firstName -> testEndpointModel("firstname", firstName,
                String.format(ServerErrors.WRONG_FIRSTNAME, validator.getMinNameLength(),
                        validator.getMaxNameLength()), params));
        testEndpointModel("firstname", "",
                ServerErrors.FIRSTNAME_NULL, params);
    }

    @Test
    public void testRegisterUserWithWrongPassword() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("firstname"));
            put("lastname", Collections.singletonList("lastname"));
            put("username", Collections.singletonList("username"));
            put("email", Collections.singletonList("asd@asd.ru"));
        }};
        List<String> passwordList = new ArrayList<>(Arrays.asList("Passworld!@3123123",
                "password!123", "password123", "Password123", "Password!"));
        passwordList.forEach(password -> testEndpointModel("password", password,
                String.format(ServerErrors.WRONG_PASSWORD, validator.getMinPasswordLength(),
                        validator.getMaxPasswordLength()), params));

        testEndpointModel("password", "",
                ServerErrors.PASSWORD_NULL, params);

        mockMvc.perform(post("/register")
                .with(csrf())
                .params(params)
                .param("password", "Passworld@1234"))
                .andDo(print())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(status().is(302))
                .andExpect(flash().attribute("ok", "true"));
    }

    @Test
    public void testInvalidCSRFToken() throws Exception {
        mockMvc.perform(post("/")
                .with(csrf().useInvalidToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testRedirectFromIndexWhenNotLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/register"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testLogin() throws Exception {
        User vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        mockMvc.perform(formLogin("/login").user(vdk64.getUsername()).password("a"))
                .andDo(print())
                .andExpect(authenticated());
        mockMvc.perform(formLogin("/login").user(vdk64.getUsername()).password("b"))
                .andDo(print())
                .andExpect(unauthenticated());
    }

    @Test
    public void testGetGuestPageWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/friend/2"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testGuestPage() throws Exception {
        User vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        User kasha111 = userRepo.findByUsername("kasha111").orElseThrow();
        User user = userRepo.findByUsername("user").orElseThrow();
        mockMvc.perform(get("/friend/3").with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("friend", kasha111))
                .andExpect(model().attribute("isFriend", true))
                .andExpect(content().string(Matchers.
                        containsString("\"sendMessageToFriend\"")));
        mockMvc.perform(get("/friend/2").with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("friend", user))
                .andExpect(model().attribute("isFriend", false));
    }

    @Test
    public void testSendMessageToFriendFromGuestPage() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("sendMessageToFriend", Collections.singletonList(""));
            put("friendId", Collections.singletonList("3"));
        }};
        User vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        User kasha111 = userRepo.findByUsername("kasha111").orElseThrow();
        User user = userRepo.findByUsername("user").orElseThrow();
        MvcResult mvcResult = mockMvc.perform(post("/friend/3").params(params)
                .with(user(vdk64)).with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();
        System.out.println();
    }
}