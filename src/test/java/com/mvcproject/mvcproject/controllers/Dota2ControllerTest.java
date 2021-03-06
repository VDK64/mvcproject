package com.mvcproject.mvcproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvcproject.mvcproject.dto.RequestData;
import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.GameStatus;
import com.mvcproject.mvcproject.entities.User;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
public class Dota2ControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private GameRepo gameRepo;
    @Value("${token}")
    private String token;
    @Value("${port_of_dota2_server}")
    private String port;
    @Value("${status_ok}")
    private String okStatus;
    @Value("${access_denied}")
    private String accessDenied;
    private final ObjectMapper mapper = new ObjectMapper();
    private User vdk64;
    private User testUser;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        testUser = userRepo.findByUsername("kasha111").orElseThrow();
    }

    private Bet getBet(User user, User opponent) {
        return betRepo.findByUserAndOpponentAndWhoWin(user, opponent, null).orElseThrow();
    }

    private void changeGameStatus(Game game, GameStatus gameStatus) {
        game.setStatus(gameStatus);
        gameRepo.save(game);
    }

    @Test
    public void testStartLobbyWithWrongToken() throws Exception {
        RequestData requestData = new RequestData("1", vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        assertNull(bet.getGame().getStatus());

        mockMvc.perform(post("/dota2/bot/start")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(accessDenied));

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertNull(betAfterRequest.getGame().getStatus());
    }

    @Test
    public void testStartLobby() throws Exception {
        RequestData requestData = new RequestData(token, vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), null);

        mockMvc.perform(post("/dota2/bot/start")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(okStatus));

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), GameStatus.STARTED);
    }

    @Test
    public void testLeaveLobbyWithWrongGameStatusNull() throws Exception {
        RequestData requestData = new RequestData(token, vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), null);

        mockMvc.perform(post("/dota2/bot/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Bet betAfterRequest = getBet(vdk64, testUser);
        changeGameStatus(betAfterRequest.getGame(), null);
    }

    @Test
    public void testLeaveLobbyWithWrongToken() throws Exception {
        RequestData requestData = new RequestData("1", vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), GameStatus.STARTED);

        mockMvc.perform(post("/dota2/bot/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(accessDenied));

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), GameStatus.STARTED);
    }

    @Test
    public void testLeaveLobby() throws Exception {
        RequestData requestData = new RequestData(token, vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), GameStatus.STARTED);

        mockMvc.perform(post("/dota2/bot/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(okStatus));

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), GameStatus.LEAVE);
    }

    @Test
    public void testPositiveLeaveWithWrongToken() throws Exception {
        RequestData requestData = new RequestData("1", vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), GameStatus.STARTED);

        mockMvc.perform(post("/dota2/bot/positiveLeave")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(accessDenied));

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), GameStatus.STARTED);
    }

    @Test
    public void testPositiveLeaveWithWrongBetStatusLeave() throws Exception {
        RequestData requestData = new RequestData(token, vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), GameStatus.LEAVE);

        mockMvc.perform(post("/dota2/bot/positiveLeave")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), GameStatus.LEAVE);
    }

    @Test
    public void testPositiveLeaveWithWrongBetStatusTimeout() throws Exception {
        RequestData requestData = new RequestData(token, vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), GameStatus.TIMEOUT);

        mockMvc.perform(post("/dota2/bot/positiveLeave")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), GameStatus.TIMEOUT);
    }

    @Test
    public void testPositiveLeave() throws Exception {
        RequestData requestData = new RequestData(token, vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), GameStatus.STARTED);

        mockMvc.perform(post("/dota2/bot/positiveLeave")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(okStatus));

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), GameStatus.POSITIVE_LEAVE);
    }

    @Test
    public void testLobbyTimeoutWithWrongToken() throws Exception {
        RequestData requestData = new RequestData("1", vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), GameStatus.STARTED);

        mockMvc.perform(post("/dota2/bot/timeout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(accessDenied));

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), GameStatus.STARTED);
    }

    @Test
    public void testLobbyTimeoutWithWrongGameStatusNull() throws Exception {
        RequestData requestData = new RequestData(token, vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), null);

        mockMvc.perform(post("/dota2/bot/timeout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), null);
    }

    @Test
    public void testLobbyTimeout() throws Exception {
        RequestData requestData = new RequestData(token, vdk64.getSteamId(),
                testUser.getSteamId(), null, port);
        String responseDataJson = mapper.writeValueAsString(requestData);
        Bet bet = getBet(vdk64, testUser);
        changeGameStatus(bet.getGame(), GameStatus.STARTED);

        mockMvc.perform(post("/dota2/bot/timeout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(responseDataJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(okStatus));

        Bet betAfterRequest = getBet(vdk64, testUser);
        assertSame(betAfterRequest.getGame().getStatus(), GameStatus.TIMEOUT);
    }
}