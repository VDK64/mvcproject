package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.validation.Validator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:test-application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class MainControllerRegisterTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Validator validator;

    @Test
    public void redirectFromIndexWhenNotLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().is(302));
        mockMvc.perform(get("/register"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void registerUserWithWrongEmail() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("firstname"));
            put("lastname", Collections.singletonList("lastname"));
            put("username", Collections.singletonList("username"));
            put("password", Collections.singletonList("Passworld@12"));
        }};

        List<String> emailList = new ArrayList<>(Arrays.asList("a", "ab", "ab@."));
        emailList.forEach(email -> {
            try {
                mockMvc.perform(post("/register")
                        .with(csrf())
                        .params(params)
                        .param("email", email))
                        .andDo(print())
                        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                        .andExpect(model().attribute("error", ServerErrors.WRONG_EMAIL));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mockMvc.perform(post("/register")
                .with(csrf())
                .params(params)
                .param("email", ""))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                .andExpect(model().attribute("error", ServerErrors.EMAIL_NULL));
    }

    @Test
    public void registerUserWithWrongUserName() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("firstname"));
            put("lastname", Collections.singletonList("lastname"));
            put("password", Collections.singletonList("Passworld@12"));
            put("email", Collections.singletonList("asd@asd.ru"));
        }};

        List<String> userNameList = new ArrayList<>(Arrays.asList("a", "фы№", "asd@",
                "asdczAsdqweFgtrAasdawq"));
        userNameList.forEach(userName -> {
            try {
                mockMvc.perform(post("/register")
                        .with(csrf())
                        .params(params)
                        .param("username", userName))
                        .andDo(print())
                        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                        .andExpect(model().attribute("error", String.format(ServerErrors.WRONG_USERNAME,
                                validator.getMinNameLength(), validator.getMaxNameLength())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        List<String> wrightUsernameList = new ArrayList<>(Arrays.asList("alts1993", "a-petrov", "p.alekseev123",
                "asd_vdk64"));

        wrightUsernameList.forEach(userName -> {
            try {
                mockMvc.perform(post("/register")
                        .with(csrf())
                        .params(params)
                        .param("username", userName))
                        .andDo(print())
                        .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"))
                        .andExpect(flash().attribute("ok", "true"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mockMvc.perform(post("/register")
                .with(csrf())
                .params(params)
                .param("username", ""))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                .andExpect(model().attribute("error", ServerErrors.USERNAME_NULL));
    }

    @Test
    public void registerUserWithWrongLastName() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("firstname"));
            put("username", Collections.singletonList("username"));
            put("password", Collections.singletonList("Passworld@12"));
            put("email", Collections.singletonList("asd@asd.ru"));
        }};

        List<String> lastNameList = new ArrayList<>(Arrays.asList("a", "фы№", "asd@", "asd1",
                "asdczAsdqweFgtrAasdawq"));
        lastNameList.forEach(lastName -> {
            try {
                mockMvc.perform(post("/register")
                        .with(csrf())
                        .params(params)
                        .param("lastname", lastName))
                        .andDo(print())
                        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                        .andExpect(model().attribute("error", String.format(ServerErrors.WRONG_LASTNAME,
                                validator.getMinNameLength(), validator.getMaxNameLength())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mockMvc.perform(post("/register")
                .with(csrf())
                .params(params)
                .param("lastname", ""))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                .andExpect(model().attribute("error", ServerErrors.LASTNAME_NULL));
    }

    @Test
    public void registerUserWithWrongFirstName() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("lastname", Collections.singletonList("lastname"));
            put("username", Collections.singletonList("username"));
            put("password", Collections.singletonList("Passworld@12"));
            put("email", Collections.singletonList("asd@asd.ru"));
        }};

        List<String> firstNameList = new ArrayList<>(Arrays.asList("a", "фы№", "asd@", "asd1",
                "asdczAsdqweFgtrAasdawq"));
        firstNameList.forEach(firstName -> {
            try {
                mockMvc.perform(post("/register")
                        .with(csrf())
                        .params(params)
                        .param("firstname", firstName))
                        .andDo(print())
                        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                        .andExpect(model().attribute("error", String.format(ServerErrors.WRONG_FIRSTNAME,
                                validator.getMinNameLength(), validator.getMaxNameLength())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mockMvc.perform(post("/register")
                .with(csrf())
                .params(params)
                .param("firstname", ""))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                .andExpect(model().attribute("error", ServerErrors.FIRSTNAME_NULL));
    }

    @Test
    public void registerUserWithWrongPassword() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("firstname"));
            put("lastname", Collections.singletonList("lastname"));
            put("username", Collections.singletonList("username"));
            put("email", Collections.singletonList("asd@asd.ru"));
        }};

        List<String> passwordList = new ArrayList<>(Arrays.asList("Passworld!@3123123",
                "password!123", "password123", "Password123", "Password!"));
        passwordList.forEach(password -> {
            try {
                mockMvc.perform(post("/register")
                        .with(csrf())
                        .params(params)
                        .param("password", password))
                        .andDo(print())
                        .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                        .andExpect(model().attribute("error", String.format(ServerErrors.WRONG_PASSWORD,
                                validator.getMinPasswordLength(), validator.getMaxPasswordLength())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        mockMvc.perform(post("/register")
                .with(csrf())
                .params(params)
                .param("password", ""))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.model().attributeExists("error"))
                .andExpect(model().attribute("error", ServerErrors.PASSWORD_NULL));

        mockMvc.perform(post("/register")
                .with(csrf())
                .params(params)
                .param("password", "Passworld@1234"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"))
                .andExpect(status().is(302))
                .andExpect(MockMvcResultMatchers.flash().attribute("ok", "true"));
    }
}