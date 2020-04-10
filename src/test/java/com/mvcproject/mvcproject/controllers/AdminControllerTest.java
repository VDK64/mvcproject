package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
public class AdminControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Value("${token}")
    private String token;
    private User vdk64;
    private User testUser;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        testUser = userRepo.findByUsername("testUser").orElseThrow();
    }

    @Test
    public void getUsersListWithoutLogin() throws Exception {
        mockMvc.perform(get("/admin/userList"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void getUsersListWithAdmin() throws Exception {
        mockMvc.perform(get("/admin/userList").with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("admin", true));
    }

    @Test
    public void getUsersListWithNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/userList").with(user(testUser)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getEditUserWithoutLogin() throws Exception {
        mockMvc.perform(get("/admin/" + vdk64.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void getEditUserWithNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/" + vdk64.getId()).with(user(testUser)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getEditUserWithAdmin() throws Exception {
        mockMvc.perform(get("/admin/" + vdk64.getId()).with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("admin", true));
    }

    @Test
    public void editUserWithoutLogin() throws Exception {
        mockMvc.perform(post("/admin/" + vdk64.getId()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void editUserWithoutCSRF() throws Exception {
        mockMvc.perform(post("/admin/" + vdk64.getId())
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void editUserWithoutAdmin() throws Exception {
        mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .with(user(testUser)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void editUserWithAdminWithoutChanges() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList(""));
            put("password", Collections.singletonList(""));
            put("authority1", Collections.singletonList("USER"));
            put("authority2", Collections.singletonList("ADMIN"));
        }};
        MvcResult mvcResult = mockMvc.perform(post("/admin/" + vdk64.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andReturn();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> users = (List<?>) model.get("users");
        Assert.assertTrue(users.contains(vdk64));
    }

    @Test
    public void editUserWithAdminChangeFirstNameInvalid() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("a"));
            put("lastname", Collections.singletonList(""));
            put("password", Collections.singletonList(""));
            put("authority1", Collections.singletonList("USER"));
        }};
        mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void editUserWithAdminChangeFirstName() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList("Firstname"));
            put("lastname", Collections.singletonList(""));
            put("password", Collections.singletonList(""));
            put("authority1", Collections.singletonList("USER"));
        }};
        MvcResult mvcResult = mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andReturn();
        testUser.setFirstname("Firstname");
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> users = (List<?>) model.get("users");
        Assert.assertTrue(users.contains(testUser));
    }

    @Test
    public void editUserWithAdminChangeLastNameInvalid() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList("a"));
            put("password", Collections.singletonList(""));
            put("authority1", Collections.singletonList("USER"));
        }};
        mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void editUserWithAdminChangeLastName() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList("Lastname"));
            put("password", Collections.singletonList(""));
            put("authority1", Collections.singletonList("USER"));
        }};
        MvcResult mvcResult = mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andReturn();
        testUser.setLastname("Lastname");
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> users = (List<?>) model.get("users");
        Assert.assertTrue(users.contains(testUser));
    }

    @Test
    public void editUserWithAdminChangePasswordInvalid() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList(""));
            put("password", Collections.singletonList("password"));
            put("authority1", Collections.singletonList("USER"));
        }};
        mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void editUserWithAdminChangePassword() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList(""));
            put("password", Collections.singletonList("Passworld@123"));
            put("authority1", Collections.singletonList("USER"));
        }};
        MvcResult mvcResult = mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andReturn();
        User newKasha111 = userRepo.findById(testUser.getId()).orElseThrow();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> users = (List<?>) model.get("users");
        Assert.assertTrue(users.contains(newKasha111));
    }

    @Test
    public void editUserWithAdminChangeRoles() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList(""));
            put("password", Collections.singletonList(""));
            put("authority1", Collections.singletonList("USER"));
            put("authority2", Collections.singletonList("ADMIN"));
        }};
        MvcResult mvcResult = mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andReturn();
        User newKasha111 = userRepo.findById(testUser.getId()).orElseThrow();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> users = (List<?>) model.get("users");
        Assert.assertTrue(users.contains(newKasha111));
    }

    @Test
    public void getTokenWithoutLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("getToken", Collections.singletonList(""));
        }};
        mockMvc.perform(post("/admin/userList")
                .params(params))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getTokenWithoutCSRF() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("getToken", Collections.singletonList(""));
        }};
        mockMvc.perform(post("/admin/userList")
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getTokenWithoutAdmin() throws Exception {
        User user = userRepo.findById(2L).orElseThrow();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("getToken", Collections.singletonList(""));
        }};
        mockMvc.perform(post("/admin/userList")
                .params(params)
                .with(csrf())
                .with(user(user)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void getTokenWithAdmin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("getToken", Collections.singletonList(""));
        }};
        MvcResult mvcResult = mockMvc.perform(post("/admin/userList")
                .params(params)
                .with(csrf())
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        Object token = model.get("token");
        Assert.assertEquals(token, this.token);
    }
}