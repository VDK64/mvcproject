package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.dto.MessageDto;
import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.MessageService;
import com.mvcproject.mvcproject.services.UserService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
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

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class FriendControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private DialogRepo dialogRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageController messageController;
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

    private Matcher<String> doesNotContainString(String s) {
        return CoreMatchers.not(containsString(s));
    }

    @Test
    public void testFriendsWithoutLogin() throws Exception {
        mockMvc.perform(get("/friends"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testFriendsWithLoginAndUnconfirmedFriends() throws Exception {
        User tony64 = userRepo.findByUsername("tony64").orElseThrow();

        MvcResult mvcResult = mockMvc.perform(get("/friends")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("invites"))
                .andReturn();

        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        Set<?> invites = (Set<?>) model.get("invites");
        assertTrue(invites.contains(tony64));
    }

    @Test
    public void testConfirmInviteWithoutLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("confirmInvite", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/friends")
                .with(csrf())
                .params(params))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testConfirmInviteWithoutCSRF() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("confirmInvite", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/friends")
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void testFriendsWithLoginAfterConfirmInviteOfFriend() throws Exception {
        User tony64 = userRepo.findByUsername("tony64").orElseThrow();

        assertTrue(tony64.getFriends().contains(vdk64));
        assertFalse(vdk64.getFriends().contains(tony64));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("confirmInvite", Collections.singletonList(""));
            put("inviteUsername", Collections.singletonList(tony64.getUsername()));
        }};

        mockMvc.perform(post("/friends")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends"));

        MvcResult mvcResult = mockMvc.perform(get("/friends")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        Set<?> invites = (Set<?>) model.get("invites");
        assertEquals(0, invites.size());
        assertTrue(vdk64.getFriends().contains(tony64));
    }

    @Test
    public void testSendMessageWithoutLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("sendMessageToFriend", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/friends")
                .with(csrf())
                .params(params))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testSendMessageWithoutCSRF() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("sendMessageToFriend", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/friends")
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testSendMessageAndReply() throws Exception {
        User user = userRepo.findById(5L).orElseThrow();

        mockMvc.perform(get("/dialogs")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(doesNotContainString(user.getUsername())));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("sendMessageToFriend", Collections.singletonList(""));
            put("friendId", Collections.singletonList(user.getId().toString()));
        }};

        MvcResult mvcResult = mockMvc.perform(post("/friends")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/messages/*"))
                .andReturn();

        String redirectedUrl = mvcResult.getResponse().getRedirectedUrl();
        String[] split = Objects.requireNonNull(redirectedUrl).split("/");
        Long dialogId = Long.valueOf(split[split.length - 1]);
        Dialog dialog = dialogRepo.findById(dialogId).orElseThrow();
        assertTrue(dialog.getUsers().contains(vdk64));
        assertTrue(dialog.getUsers().contains(user));

        mockMvc.perform(get("/dialogs")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(user.getUsername())));

        mockMvc.perform(get("/dialogs").with(user(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(doesNotContainString(vdk64.getUsername())));

        params.replace("friendId", Collections.singletonList(vdk64.getId().toString()));

        mockMvc.perform(post("/friends")
                .with(csrf())
                .with(user(user))
                .params(params))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/messages/*"));

        assertTrue(dialog.getUsers().contains(vdk64));
        assertTrue(dialog.getUsers().contains(user));

        mockMvc.perform(get("/dialogs").with(user(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(vdk64.getUsername())));

        dialogRepo.deleteById(dialogId);
    }

    @Test
    public void testGetFindFriendsWithoutLogin() throws Exception {
        mockMvc.perform(get("/friends/find_friends"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testGetFindFriends() throws Exception {
        mockMvc.perform(get("/friends/find_friends")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testFindFriendsWithoutLogin() throws Exception {
        mockMvc.perform(post("/friends/find_friends")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testFindFriendsWithoutCSRF() throws Exception {
        mockMvc.perform(post("/friends/find_friends")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testFindFriendsStranger() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("username", Collections.singletonList("testUser"));
        }};

        mockMvc.perform(post("/friends/find_friends")
                .params(params)
                .with(csrf())
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("findUser"))
                .andExpect(model().attribute("findUser", testUser))
                .andExpect(model().attribute("isFriend", false))
                .andExpect(content().string(containsString("add to friends")));
    }

    @Test
    public void testFindFriends() throws Exception {
        User kasha111 = userRepo.findByUsername("kasha111").orElseThrow();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("username", Collections.singletonList("kasha111"));
        }};

        mockMvc.perform(post("/friends/find_friends")
                .params(params)
                .with(csrf())
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("findUser"))
                .andExpect(model().attribute("findUser", kasha111))
                .andExpect(model().attribute("isFriend", true))
                .andExpect(content().string(doesNotContainString("add to friends")));
    }

    @Test
    public void testAddFriendWithoutLogin() throws Exception {
        mockMvc.perform(post("/friends/find_friends")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @Transactional
    public void testAddFriendWithoutCSRF() throws Exception {
        mockMvc.perform(post("/friends/find_friends")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void testAddFriend() throws Exception {
        assertFalse(vdk64.getFriends().contains(testUser));
        assertFalse(testUser.getFriends().contains(vdk64));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("addFriend", Collections.singletonList(""));
            put("username", Collections.singletonList(testUser.getUsername()));
        }};

        mockMvc.perform(post("/friends/find_friends")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(vdk64.getFriends().contains(testUser));

        mockMvc.perform(get("/friends")
                .with(user(testUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("invites", Stream.of(vdk64).collect(Collectors.toSet())));

        params.replace("username", Collections.singletonList(vdk64.getUsername()));

        mockMvc.perform(post("/friends/find_friends")
                .with(csrf())
                .with(user(testUser))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(testUser.getFriends().contains(vdk64));
    }

    @Test
    public void testFindYourSelf() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("username", Collections.singletonList(vdk64.getUsername()));
        }};

        mockMvc.perform(post("/friends/find_friends")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(doesNotContainString("add to friends")));
    }

    @Test
    public void testDialogIsAppearWhenSendNewMessage() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("sendMessageToFriend", Collections.singletonList(""));
            put("friendId", Collections.singletonList("5"));
        }};

        User vasily = userRepo.findById(5L).orElseThrow();

        MvcResult mvcResult = mockMvc.perform(post("/friends")
                .params(params)
                .with(csrf())
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/messages/*"))
                .andReturn();

        String[] splitUrl = Objects.requireNonNull(mvcResult
                .getResponse()
                .getRedirectedUrl())
                .split("/");
        Long dialogId = Long.valueOf(splitUrl[splitUrl.length - 1]);

        MessageDto messageDto = new MessageDto("vdk64", "vasiliy228",
                "Hello, Vasily! It's a test message.", new Date().toString(), dialogId);

        mockMvc.perform(get("/dialogs")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("vasiliy228")));

        mockMvc.perform(get("/dialogs")
                .with(user(vasily)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sorry, no Dialogs")));

        messageController.sendSpecific(messageDto);

        mockMvc.perform(get("/dialogs")
                .with(user(vasily)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(doesNotContainString("Sorry, no Dialogs")))
                .andExpect(content().string(containsString("vdk64")));

        dialogRepo.deleteById(dialogId);

    }
}