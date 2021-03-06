package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.Role;
import com.mvcproject.mvcproject.entities.ShowStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.MessageRepo;
import com.mvcproject.mvcproject.repositories.ShowStatusRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
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
    @Value("${success.confirm}")
    private String ok;
    @Value("${wrong.confirm}")
    private String wringConfirm;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @Autowired
    private Validator validator;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ShowStatusRepo showStatusRepo;
    @Autowired
    private DialogRepo dialogRepo;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private User vdk64;
    private User kasha111;
    private User user;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        kasha111 = userRepo.findByUsername("kasha111").orElseThrow();
        user = userRepo.findByUsername("user").orElseThrow();
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
        mockMvc.perform(formLogin("/login").user(vdk64.getUsername()).password("a"))
                .andDo(print())
                .andExpect(authenticated());
        mockMvc.perform(formLogin("/login").user(vdk64.getUsername()).password("b"))
                .andDo(print())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrlPattern("/login?error"));
    }

    @Test
    public void testLoginAndMainPage() throws Exception {
        mockMvc.perform(get("/").with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"));
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
        mockMvc.perform(get("/friend/3").with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("friend", kasha111))
                .andExpect(model().attribute("isFriend", true))
                .andExpect(content().string(Matchers.
                        containsString("\"sendMessageToFriend\"")));

        mockMvc.perform(get("/friend/2").with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("friend", user))
                .andExpect(model().attribute("isFriend", false));
    }

    @Test
    public void testSendMessageToFriendFromGuestPage() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("sendMessageToFriend", Collections.singletonList(""));
            put("friendId", Collections.singletonList("3"));
        }};

        mockMvc.perform(post("/friend/3").params(params)
                .with(user(vdk64)).with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages/1"));
    }

    @Test
    public void testConfirmEmail() throws Exception {
        String activationCode = "activation001";
        String password = "Passworld@123";
        User vanchk64 = new User(null, "Petr", "Ivanchenko", "vanchk64",
                "yenmbgvf@10mail.org", activationCode,"default",
                Stream.of(Role.USER).collect(Collectors.toSet()),
                passwordEncoder.encode(password), true, true, true,
                false, null, null, 100F, null, null, false,
                null, false, false);
        userRepo.save(vanchk64);

        mockMvc.perform(formLogin("/login")
                .user(vanchk64.getUsername())
                .password(password))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated());

        mockMvc.perform(get("/email/activate/ascasd123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("msg", wringConfirm));

        mockMvc.perform(get("/email/activate/" + activationCode))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("msg", ok));

        vanchk64 = userRepo.findByUsername("vanchk64").orElseThrow();
        assertNull(vanchk64.getActivationCode());

        mockMvc.perform(formLogin("/login")
                .user(vanchk64.getUsername())
                .password(password))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated());
    }

    @Test
    public void testLoginMethod() throws Exception {
        mockMvc.perform(get("/login?error="))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "credentials"));

        mockMvc.perform(get("/login?error=disabled"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", "disabled"));
    }


    @Test
    @Transactional
    public void testSendMessageFromMainPage() throws Exception {
        List<ShowStatus> byDialogId = showStatusRepo.findByDialogId(1L);
        assertEquals(2, byDialogId.size());
        assertTrue(byDialogId.stream().noneMatch(showStatus -> showStatus.getDialog().getHaveNewMessages()));
        assertTrue(byDialogId.stream().allMatch(ShowStatus::isVisible));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("sendMessageToFriend", Collections.singletonList(""));
            put("friendId", Collections.singletonList("3"));
        }};

        mockMvc.perform(post("/friend/3").params(params)
                .with(user(vdk64)).with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages/1"));
    }
}