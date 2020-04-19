package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mvcproject.mvcproject.common.CustomMatcher.doesNotContainString;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
public class BetControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepo userRepo;
    @Value("${without_steamId}")
    private String withoutSteamId;
    @Value("${create_button_text}")
    private String createButtonText;
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
    public void testBetsWithoutLogin() throws Exception {
        mockMvc.perform(get("/bets"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testBetsWithoutSteamId() throws Exception {
        mockMvc.perform(get("/bets")
                .with(user(testUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(content().string(containsString(withoutSteamId)));
    }

    @Test
    public void testBetsWithSteamId() throws Exception {
        mockMvc.perform(get("/bets")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(content().string(doesNotContainString(withoutSteamId)))
                .andExpect(content().string(containsString(createButtonText)));
    }

    @Test
    public void testBetsDetailsOwner() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bets")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(content().string(doesNotContainString(withoutSteamId)))
                .andExpect(content().string(containsString(createButtonText)))
                .andReturn();
        checkDetailsExist(mvcResult);
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void TestGetTableWithoutCSRF() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("chooseTable", Collections.singletonList(""));
            put("table", Collections.singletonList("opponent"));
        }};

        mockMvc.perform(post("/bets")
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void TestGetTableWithoutLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("chooseTable", Collections.singletonList(""));
            put("table", Collections.singletonList("opponent"));
        }};

        mockMvc.perform(post("/bets")
                .params(params)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void TestGetTableOwnerWithoutSteamIdOwner() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("chooseTable", Collections.singletonList(""));
            put("table", Collections.singletonList("owner"));
        }};

        MvcResult mvcResult = mockMvc.perform(post("/bets")
                .with(csrf())
                .with(user(testUser))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(content().string(containsString(withoutSteamId)))
                .andExpect(content().string(doesNotContainString(createButtonText)))
                .andReturn();
        checkDetailsExist(mvcResult);
    }

    @Test
    public void TestGetTableOwnerWithoutSteamIdOpponent() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("chooseTable", Collections.singletonList(""));
            put("table", Collections.singletonList("opponent"));
        }};

        MvcResult mvcResult = mockMvc.perform(post("/bets")
                .with(csrf())
                .with(user(testUser))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(content().string(containsString(withoutSteamId)))
                .andExpect(content().string(doesNotContainString(createButtonText)))
                .andReturn();
        checkDetailsExist(mvcResult);
    }

    @Test
    public void TestGetTableOwnerWithSteamIdOwner() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("chooseTable", Collections.singletonList(""));
            put("table", Collections.singletonList("owner"));
        }};

        MvcResult mvcResult = mockMvc.perform(post("/bets")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(content().string(doesNotContainString(withoutSteamId)))
                .andExpect(content().string(containsString(createButtonText)))
                .andReturn();
        checkDetailsExist(mvcResult);
    }

    @Test
    public void TestGetTableOwnerWithSteamIdOpponent() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("chooseTable", Collections.singletonList(""));
            put("table", Collections.singletonList("opponent"));
        }};

        MvcResult mvcResult = mockMvc.perform(post("/bets")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(content().string(doesNotContainString(withoutSteamId)))
                .andExpect(content().string(containsString(createButtonText)))
                .andReturn();
        checkDetailsExist(mvcResult);
    }

//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void TestBetsTable() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("tablePage", Collections.singletonList(""));
            put("table", Collections.singletonList("opponent"));
        }};

        MvcResult mvcResult = mockMvc.perform(post("/bets")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(content().string(doesNotContainString(withoutSteamId)))
                .andExpect(content().string(containsString(createButtonText)))
                .andReturn();
        checkDetailsExist(mvcResult);
    }

    @Test
    public void createBet() {
    }

    @Test
    public void testCreateBet() {
    }

    @Test
    public void betNotification() {
    }

    @Test
    public void getDetails() {
    }

    private void checkDetailsExist(MvcResult mvcResult) throws UnsupportedEncodingException {
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> items = (List<?>) model.get("items");
        boolean boo = items.stream().anyMatch(item -> item instanceof Bet && ((Bet) item).getWhoWin() == null);
        if (boo)
            assertTrue(mvcResult.getResponse().getContentAsString().contains("See details"));
        else
            assertFalse(mvcResult.getResponse().getContentAsString().contains("See details"));
    }
}