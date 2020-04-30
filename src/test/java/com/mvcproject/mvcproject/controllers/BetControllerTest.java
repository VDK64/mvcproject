package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.BetRepo;
import com.mvcproject.mvcproject.repositories.GameRepo;
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

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mvcproject.mvcproject.common.CustomMatcher.doesNotContainString;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
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
public class BetControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private GameRepo gameRepo;
    @Value("${without_steamId}")
    private String withoutSteamId;
    @Value("${create_button_text}")
    private String createButtonText;
    @Value("${regex_enable_details}")
    private String enableDetailsButton;
    @Value("${regex_disable_details}")
    private String disableDetailsButton;
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

    @Test
    public void TestGetTableWithoutCSRF() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
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

    @Test
    public void TestBetsTableWithoutLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("tablePage", Collections.singletonList(""));
            put("tableName", Collections.singletonList("Owner"));
            put("page", Collections.singletonList("3"));
        }};

        mockMvc.perform(post("/bets")
                .with(csrf())
                .params(params))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void TestBetsTableWithoutCSRF() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("tablePage", Collections.singletonList(""));
            put("tableName", Collections.singletonList("Owner"));
            put("page", Collections.singletonList("3"));
        }};

        mockMvc.perform(post("/bets")
                .with(user(testUser))
                .params(params))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void TestBetsTableOwner() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("tablePage", Collections.singletonList(""));
            put("tableName", Collections.singletonList("Owner"));
            put("page", Collections.singletonList("3"));
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
                .andExpect(model().attribute("totalPages", 3))
                .andExpect(model().attribute("currentPage", 3))
                .andExpect(content().string(containsString(withoutSteamId)))
                .andExpect(content().string(doesNotContainString(createButtonText)))
                .andReturn();
        checkDetailsExist(mvcResult);
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> items = (List<?>) model.get("items");
        Bet bet = (Bet) items.get(items.size() - 1);
        assertEquals(28, bet.getValue(), 0.0);
    }

    @Test
    public void TestBetsTableOpponent() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("tablePage", Collections.singletonList(""));
            put("tableName", Collections.singletonList("opponent"));
            put("page", Collections.singletonList("4"));
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
                .andExpect(model().attribute("totalPages", 4))
                .andExpect(model().attribute("currentPage", 4))
                .andExpect(content().string(containsString(withoutSteamId)))
                .andExpect(content().string(doesNotContainString(createButtonText)))
                .andReturn();
        checkDetailsExist(mvcResult);
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> items = (List<?>) model.get("items");
        Bet bet = (Bet) items.get(items.size() - 1);
        assertEquals(200, bet.getValue(), 0.0);
    }

    @Test
    public void createBetWithoutLogin() throws Exception {
        mockMvc.perform(get("/bets/createBet"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @Transactional
    public void createBet() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/bets/createBet")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andReturn();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> friends = (List<?>) model.get("friends");
        List<?> friendsFromDb = new ArrayList<>(vdk64.getFriends());
        assertTrue(friends.containsAll(friendsFromDb));
    }

    @Test
    public void testCreateBetWithoutLogin() throws Exception {
        mockMvc.perform(post("/bets/createBet")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testCreateBetWithoutCSRF() throws Exception {
        mockMvc.perform(post("/bets/createBet")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateExistingBet() throws Exception {
        List<Bet> all = (List<Bet>) betRepo.findAll();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("game", Collections.singletonList("Dota2"));
            put("gamemode", Collections.singletonList("1x1"));
            put("value", Collections.singletonList("1"));
            put("opponent", Collections.singletonList("Аркадий kasha111 Ротенберг"));
            put("lobbyName", Collections.singletonList("a"));
            put("password", Collections.singletonList("a"));
        }};

        mockMvc.perform(post("/bets/createBet")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", ServerErrors.BET_EXIST));
        List<Bet> afterWrongCreateBet = (List<Bet>) betRepo.findAll();
        assertEquals(all.size(), afterWrongCreateBet.size());
    }

    @Test
    public void testCreateBetWithVeryBigValue() throws Exception {
        List<Bet> all = (List<Bet>) betRepo.findAll();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("game", Collections.singletonList("Dota2"));
            put("gamemode", Collections.singletonList("1x1"));
            put("value", Collections.singletonList("10000"));
            put("opponent", Collections.singletonList("Василий vasiliy228 Самойлов"));
            put("lobbyName", Collections.singletonList("myLobby"));
            put("password", Collections.singletonList("pass"));
        }};

        mockMvc.perform(post("/bets/createBet")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", ServerErrors.WRONG_BET_VALUE));
        List<Bet> afterWrongCreateBet = (List<Bet>) betRepo.findAll();
        assertEquals(all.size(), afterWrongCreateBet.size());
    }

    @Test
    public void testCreateBetWithIncorrectValue() throws Exception {
        List<Bet> all = (List<Bet>) betRepo.findAll();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("game", Collections.singletonList("Dota2"));
            put("gamemode", Collections.singletonList("1x1"));
            put("value", Collections.singletonList("-1"));
            put("opponent", Collections.singletonList("Василий vasiliy228 Самойлов"));
            put("lobbyName", Collections.singletonList("myLobby"));
            put("password", Collections.singletonList("pass"));
        }};

        mockMvc.perform(post("/bets/createBet")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", ServerErrors.WRONG_VALUE));
        List<Bet> afterWrongCreateBet = (List<Bet>) betRepo.findAll();
        assertEquals(all.size(), afterWrongCreateBet.size());
    }

    @Test
    public void testCreateBetWithEmptyPassword() throws Exception {
        List<Bet> all = (List<Bet>) betRepo.findAll();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("game", Collections.singletonList("Dota2"));
            put("gamemode", Collections.singletonList("1x1"));
            put("value", Collections.singletonList("1"));
            put("opponent", Collections.singletonList("Василий vasiliy228 Самойлов"));
            put("lobbyName", Collections.singletonList("myLobby"));
            put("password", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/bets/createBet")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", ServerErrors.LOBBYPASSWORD_NULL));
        List<Bet> afterWrongCreateBet = (List<Bet>) betRepo.findAll();
        assertEquals(all.size(), afterWrongCreateBet.size());
    }

    @Test
    public void testCreateBetWithEmptyLobbyName() throws Exception {
        List<Bet> all = (List<Bet>) betRepo.findAll();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("game", Collections.singletonList("Dota2"));
            put("gamemode", Collections.singletonList("1x1"));
            put("value", Collections.singletonList("1"));
            put("opponent", Collections.singletonList("Василий vasiliy228 Самойлов"));
            put("lobbyName", Collections.singletonList(""));
            put("password", Collections.singletonList("pass"));
        }};

        mockMvc.perform(post("/bets/createBet")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", ServerErrors.LOBBYNAME_NULL));
        List<Bet> afterWrongCreateBet = (List<Bet>) betRepo.findAll();
        assertEquals(all.size(), afterWrongCreateBet.size());
    }

    @Test
    @Transactional
    public void testCreateBet() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("game", Collections.singletonList("Dota2"));
            put("gamemode", Collections.singletonList("1x1"));
            put("value", Collections.singletonList("1"));
            put("opponent", Collections.singletonList("Василий vasiliy228 Самойлов"));
            put("lobbyName", Collections.singletonList("lobby"));
            put("password", Collections.singletonList("pass"));
        }};
        Float deposit = userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getDeposit();

        mockMvc.perform(post("/bets/createBet")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/bets"));
        User vasiliy = userRepo.findByUsername("vasiliy228").orElseThrow();
        Bet bet = betRepo.findByUserAndOpponentAndWhoWin(vdk64, vasiliy, null).orElseThrow();
        Game game = bet.getGame();
        assertEquals("lobby", game.getLobbyName());
        assertEquals("pass", game.getPassword());
        assertEquals("1x1", game.getGameMode());
        assertEquals("lobby", game.getLobbyName());
        assertEquals(deposit - 1, userRepo.findByUsername(vdk64.getUsername())
                .orElseThrow().getDeposit(), 0.0);

        mockMvc.perform(get("/bets")
                .with(user(vasiliy)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("newBets", true))
                .andExpect(model().attribute("tableName", "Opponent"))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(content().string(containsString(" <td><b>vdk64</b></td>")))
                .andExpect(content().string(doesNotContainString("See details")));
    }

    @Test
    public void getDetailsWithoutLogin() throws Exception {
        mockMvc.perform(get("/bets/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void getDetails() throws Exception {
        mockMvc.perform(get("/bets/1")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("admin"))
                .andExpect(content().string(containsString("Opponent must confirm this bet at first!!!")));
    }

    @Test
    public void getWrongDetails() throws Exception {
        mockMvc.perform(get("/bets/159")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(view().name("errorPage"));
    }

    @Test
    public void getWrongDetails2() throws Exception {
        mockMvc.perform(get("/bets/5")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(view().name("errorPage"));
    }

    @Test
    public void betNotificationWithoutLogin() throws Exception {
        User testUser = userRepo.findByUsername("testUser").orElseThrow();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("confirmBet", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/bets/" + 64L)
                .params(params)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void betNotificationWithoutCSRF() throws Exception {
        User testUser = userRepo.findByUsername("testUser").orElseThrow();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("confirmBet", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/bets/" + 64L)
                .params(params)
                .with(user(testUser)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void betNotificationWithoutSteamId() throws Exception {
        User testUser = userRepo.findByUsername("testUser").orElseThrow();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("confirmBet", Collections.singletonList(""));
        }};

        MvcResult mvcResult = mockMvc.perform(post("/bets/" + 64L)
                .params(params)
                .with(csrf())
                .with(user(testUser)))
                .andDo(print())
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attribute("error", ServerErrors.STEAM_ID_NULL))
                .andExpect(content().string(doesNotContainString("Create bet")))
                .andReturn();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        Bet bet = betRepo.findById(64L).orElseThrow();
        User user = (User) model.get("user");
        assertFalse(bet.getIsConfirm());
        assertEquals(user.getDeposit(), 100, 0.0);
    }

    @Test
    public void betNotificationWithoutMoney() throws Exception {
        User testUser = userRepo.findByUsername("testUser").orElseThrow();
        testUser.setSteamId("123");
        testUser = userRepo.save(testUser);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("confirmBet", Collections.singletonList(""));
        }};

        MvcResult mvcResult = mockMvc.perform(post("/bets/" + 64L)
                .params(params)
                .with(csrf())
                .with(user(testUser)))
                .andDo(print())
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attribute("error", ServerErrors.NOT_ENOUGH_MONEY))
                .andExpect(content().string(doesNotContainString("Create bet")))
                .andReturn();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        Bet bet = betRepo.findById(64L).orElseThrow();
        User user = (User) model.get("user");
        assertFalse(bet.getIsConfirm());
        assertEquals(user.getDeposit(), 100, 0.0);

        testUser.setSteamId(null);
        userRepo.save(testUser);
    }

    @Test
    public void betNotification() throws Exception {
        User kasha111 = userRepo.findByUsername("kasha111").orElseThrow();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("confirmBet", Collections.singletonList(""));
        }};
        MvcResult mvcResult = mockMvc.perform(post("/bets/" + 1L)
                .params(params)
                .with(csrf())
                .with(user(kasha111)))
                .andDo(print())
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(content().string(doesNotContainString("Create bet")))
                .andReturn();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        Bet bet = (Bet) model.get("bet");
        User user = (User) model.get("user");
        assertTrue(bet.getIsConfirm());
        assertEquals(user.getDeposit(), 550, 0.0);
    }

    @Test
    public void testDeleteBet() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("deleteBet", Collections.singletonList(""));
            put("betId", Collections.singletonList("65"));
            put("table", Collections.singletonList("Owner"));
        }};

        MvcResult mvcResult = mockMvc.perform(post("/bets")
                .params(params)
                .with(user(vdk64))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Optional<Bet> bet = betRepo.findById(65L);
        Optional<Game> game = gameRepo.findById(3L);
        assertFalse(bet.isPresent());
        assertFalse(game.isPresent());
    }

    private void checkDetailsExist(MvcResult mvcResult) throws UnsupportedEncodingException {
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> items = (List<?>) model.get("items");
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Pattern disabledPattern = Pattern.compile(disableDetailsButton);
        Pattern enabledPattern = Pattern.compile(enableDetailsButton);
        Matcher disabledMatcher = disabledPattern.matcher(contentAsString);
        Matcher enabledMatcher = enabledPattern.matcher(contentAsString);
        boolean boo = items.stream().anyMatch(item -> item instanceof Bet && ((Bet) item).getWhoWin() == null);
        if (boo)
            assertTrue(enabledMatcher.find());
        else
            assertTrue(disabledMatcher.find());
    }
}