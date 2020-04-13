package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.dto.MessageDto;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.junit.Before;
import org.junit.BeforeClass;
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

import javax.transaction.Transactional;
import java.util.*;

import static com.mvcproject.mvcproject.common.CustomMatcher.doesNotContainString;
import static org.hamcrest.Matchers.containsString;
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
public class MessageControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageController messageController;
    private User vdk64;
    private User kasha;
    private static final String messageText = "Kasha, it is a test message!";
    private final Long petroDialog = 2L;
    private static final Long kashaDialog = 1L;
    private static MessageDto messageDto;

    @BeforeClass
    public static void initClass() {
        messageDto = new MessageDto("vdk64", "kasha111", messageText,
                new Date().toString(), kashaDialog);
    }

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
        User testUser = userRepo.findByUsername("petro123").orElseThrow();
        kasha = userRepo.findByUsername("kasha111").orElseThrow();
    }

    @Test
    public void testGetDialogsWithoutLogin() throws Exception {
        mockMvc.perform(get("/dialogs"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testGetDialogs() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/dialogs")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("dialogs"))
                .andReturn();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        Set<?> dialogs = (Set<?>) model.get("dialogs");
        assertEquals(2, dialogs.size());
    }

    @Test
    public void testDeleteDialogWithoutLogin() throws Exception {
        mockMvc.perform(post("/dialogs")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testDeleteDialogWithoutCSRF() throws Exception {
        mockMvc.perform(post("/dialogs")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void testDeleteDialog() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            put("dialogId", Collections.singletonList(petroDialog.toString()));
        }};

        mockMvc.perform(post("/dialogs")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dialogs"));

        MvcResult mvcResult = mockMvc.perform(get("/dialogs")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("dialogs"))
                .andReturn();

        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        Set<?> dialogs = (Set<?>) model.get("dialogs");
        assertEquals(1, dialogs.size());
    }

    @Test
    @Transactional
    public void testGetMessagesWithoutLogin() throws Exception {
        mockMvc.perform(get("/messages/" + kashaDialog.toString()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @Transactional
    public void testGetMessages() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/messages/" + kashaDialog.toString())
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> messages = (List<?>) model.get("messages");
        MessageDto messageDto = (MessageDto) messages.get(0);
        assertTrue(messageDto.getText().contains("Kasha"));
    }

    @Test
    public void testSendSpecific() throws Exception {
        messageController.sendSpecific(messageDto);

        MvcResult mvcResult = mockMvc.perform(get("/messages/" + kashaDialog.toString())
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> model = Objects.requireNonNull(mvcResult.getModelAndView()).getModel();
        List<?> messages = (List<?>) model.get("messages");
        MessageDto messageDto2 = (MessageDto) messages.get(messages.size() - 1);
        assertEquals(6, messages.size());
        assertTrue(messageDto2.getText().contains(messageText));

        mockMvc.perform(get("/dialogs").with(user(kasha)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("vdk64-new")));

        messageController.updateMessage(kasha, messageDto);

        mockMvc.perform(get("/dialogs").with(user(kasha)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(doesNotContainString("vdk64-new")));
    }
}