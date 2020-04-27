package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import freemarker.template.utility.StringUtil;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    private MultiValueMap<String, String> formParams(String firstname,
                                                     String lastname,
                                                     String password,
                                                     String authority1,
                                                     String authority2) {
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        if (StringUtil.emptyToNull(firstname) != null)
            result.put("firstname", Collections.singletonList(firstname));
        else
            result.put("firstname", Collections.singletonList(""));
        if (StringUtil.emptyToNull(lastname) != null)
            result.put("lastname", Collections.singletonList(lastname));
        else
            result.put("lastname", Collections.singletonList(""));
        if (StringUtil.emptyToNull(password) != null)
            result.put("password", Collections.singletonList(password));
        else
            result.put("password", Collections.singletonList(""));
        if (StringUtil.emptyToNull(authority1) != null)
            result.put("authority1", Collections.singletonList(authority1));
        if (StringUtil.emptyToNull(authority2) != null)
            result.put("authority2", Collections.singletonList(authority2));
        return result;
    }

    @Test
    public void testGetUsersListWithoutLogin() throws Exception {
        mockMvc.perform(get("/admin/userList"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testGetUsersListWithAdmin() throws Exception {
        mockMvc.perform(get("/admin/userList").with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("admin", true));
    }

    @Test
    public void testGetUsersListWithNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/userList").with(user(testUser)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetEditUserWithoutLogin() throws Exception {
        mockMvc.perform(get("/admin/" + vdk64.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testGetEditUserWithNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/" + vdk64.getId()).with(user(testUser)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetEditUserWithAdmin() throws Exception {
        mockMvc.perform(get("/admin/" + vdk64.getId()).with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("admin", true));
    }

    @Test
    public void testEditUserWithoutLogin() throws Exception {
        mockMvc.perform(post("/admin/" + vdk64.getId())
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testEditUserWithoutCSRF() throws Exception {
        mockMvc.perform(post("/admin/" + vdk64.getId())
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testEditUserWithoutAdmin() throws Exception {
        mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .with(user(testUser)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testEditUserWithAdminWithoutChanges() throws Exception {
        MultiValueMap<String, String> params = formParams(null, null,
                null, "ADMIN", "USER");

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
        assertTrue(users.contains(vdk64));
    }

    @Test
    public void testEditUserWithAdminChangeFirstNameInvalid() throws Exception {
        MultiValueMap<String, String> params = formParams("a", null,
                null, "USER", null);

        mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testEditUserWithAdminChangeFirstName() throws Exception {
        MultiValueMap<String, String> params = formParams("Firstname", null,
                null, "USER", null);

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
        assertTrue(users.contains(testUser));
    }

    @Test
    public void testEditUserWithAdminChangeLastNameInvalid() throws Exception {
        MultiValueMap<String, String> params = formParams(null, "a",
                null, "USER", null);

        mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testEditUserWithAdminChangeLastName() throws Exception {
        MultiValueMap<String, String> params = formParams(null, "Lastname",
                null, "USER", null);

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
        assertTrue(users.contains(testUser));
    }

    @Test
    public void testEditUserWithAdminChangePasswordInvalid() throws Exception {
        MultiValueMap<String, String> params = formParams(null, null,
                "password", "USER", null);

        mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void testEditUserWithAdminChangePassword() throws Exception {
        MultiValueMap<String, String> params = formParams(null, null,
                "Passworld@123", "USER", null);

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
        assertTrue(users.contains(newKasha111));
    }

    @Test
    public void testEditUserWithAdminChangeRoles() throws Exception {
        MultiValueMap<String, String> params = formParams(null, null,
                null, "USER", "ADMIN");

        MvcResult mvcResult = mockMvc.perform(post("/admin/" + testUser.getId())
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andReturn();

        User testUser = userRepo.findById(this.testUser.getId()).orElseThrow();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> users = (List<?>) model.get("users");
        assertTrue(users.contains(testUser));
    }

    @Test
    public void testGetTokenWithoutLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("getToken", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/admin/userList")
                .with(csrf())
                .params(params))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testGetTokenWithoutCSRF() throws Exception {
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
    public void testGetTokenWithoutAdmin() throws Exception {
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
    public void testGetTokenWithAdmin() throws Exception {
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
        assertEquals(token, this.token);
    }
}