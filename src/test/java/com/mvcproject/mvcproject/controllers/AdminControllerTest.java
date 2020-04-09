package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private User vdk64;
    private User kasha111;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        kasha111 = userRepo.findByUsername("kasha111").orElseThrow();
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
        mockMvc.perform(get("/admin/userList").with(user(kasha111)))
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
        mockMvc.perform(get("/admin/" + vdk64.getId()).with(user(kasha111)))
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
        mockMvc.perform(get("/admin/" + vdk64.getId()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void editUserWithNotAdmin() throws Exception {
        mockMvc.perform(get("/admin/" + vdk64.getId()).with(user(kasha111)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

//    @Test
//    public void editUserWithAdmin() throws Exception {
//        mockMvc.perform(get("/admin/" + vdk64.getId()).with(user(vdk64)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("admin", true));
//    }

    @Test
    public void getToken() {
    }
}