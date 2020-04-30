package com.mvcproject.mvcproject.controllers;

import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.ServerErrors;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.services.SettingsService;
import com.mvcproject.mvcproject.validation.Validator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mvcproject.mvcproject.common.CustomMatcher.doesNotContainString;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
public class SettingsControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private Validator validator;
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${open_id_url}")
    private String openIdUrl;
    private User vdk64;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        vdk64 = userRepo.findByUsername("vdk64").orElseThrow();
    }

    @Test
    public void getSettingsWithoutLogin() throws Exception {
        mockMvc.perform(get("/settings"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void getSettingsWithoutOpenID() throws Exception {
        mockMvc.perform(get("/settings")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("admin"))
                .andExpect(content().string(doesNotContainString("Add your SteamId by pushing this:")));
    }

    @Test
    public void getSettingsWithOpenID() throws Exception {
        String steamId = vdk64.getSteamId();
        vdk64.setSteamId(null);
        userRepo.save(vdk64);
        mockMvc.perform(get("/settings")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("admin"))
                .andExpect(content().string(containsString("Add your SteamId by pushing this:")));
        vdk64.setSteamId(steamId);
        userRepo.save(vdk64);
    }

    @Test
    public void getSettingsWithOpenID2() throws Exception {
        String steamId = vdk64.getSteamId();
        vdk64.setSteamId(null);
        userRepo.save(vdk64);
        mockMvc.perform(get(openIdUrl)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("admin"))
                .andExpect(content().string(doesNotContainString("Add your SteamId by pushing this:")));
        assertNotNull(userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getSteamId());
        vdk64.setSteamId(steamId);
        userRepo.save(vdk64);
    }

    @Test
    public void setAvatarWithoutLogin() throws Exception {
        MockMultipartFile myFile = new MockMultipartFile("file", "myFile",
                "multipart/form-data", new byte[5]);
        mockMvc.perform(multipart("/settings")
                .file(myFile)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void setAvatarWithoutCSRF() throws Exception {
        MockMultipartFile myFile = new MockMultipartFile("file", "myFile",
                "multipart/form-data", new byte[5]);
        mockMvc.perform(multipart("/settings")
                .file(myFile)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void setAvatarOnDefault() throws Exception {
        createFileAndSetAvatar(vdk64);
        User vdkWithAvatar = userRepo.findByUsername(this.vdk64.getUsername()).orElseThrow();
        User removeAvatarUser = userRepo.save(vdk64);
        File file = new File(uploadPath + "/" + vdkWithAvatar.getId() + "/" + vdkWithAvatar.getAvatar());
        assertTrue(file.exists());
        assertNotEquals("default", vdkWithAvatar.getAvatar());
        boolean removeResult = deleteFileAndDirectory(vdkWithAvatar);
        assertTrue(removeResult);
        assertEquals("default", removeAvatarUser.getAvatar());
    }

    @Test
    public void setAvatarOnAvatar() throws Exception {
        for (int i = 0; i < 10; i++) {
            createFileAndSetAvatar(vdk64);
            User vdkWithAvatar = userRepo.findByUsername(this.vdk64.getUsername()).orElseThrow();
            File file = new File(uploadPath + "/" + vdkWithAvatar.getId() + "/" + vdkWithAvatar.getAvatar());
            assertTrue(file.exists());
        }
        User vdkWithAvatar = userRepo.findByUsername(this.vdk64.getUsername()).orElseThrow();
        User removeAvatarUser = userRepo.save(vdk64);
        File file = new File(uploadPath + "/" + vdkWithAvatar.getId() + "/" + vdkWithAvatar.getAvatar());
        assertTrue(file.exists());
        assertNotEquals("default", vdkWithAvatar.getAvatar());
        boolean removeResult = deleteFileAndDirectory(vdkWithAvatar);
        assertTrue(removeResult);
        assertEquals("default", removeAvatarUser.getAvatar());
    }

    @Test
    public void deleteAvatarWithoutLogin() throws Exception {
        mockMvc.perform(post("/settings")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void deleteAvatarWithoutCSRF() throws Exception {
        mockMvc.perform(post("/settings")
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteDefaultAvatar() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("button", Collections.singletonList(""));
        }};
        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", ServerErrors.DEFAULT_AVATAR));
    }

    @Test
    public void deleteAvatar() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("button", Collections.singletonList(""));
        }};
        createFileAndSetAvatar(vdk64);
        User userWithAvatar = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertNotEquals("default", userWithAvatar.getAvatar());
        File file = new File(uploadPath + "/" + userWithAvatar.getId() + "/" + userWithAvatar.getAvatar());
        assertTrue(file.exists());
        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeDoesNotExist("error"));
        assertFalse(file.exists());
        User userWithDeletedAvatar = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertEquals("default", userWithDeletedAvatar.getAvatar());
    }

    @Test
    public void setSettingsWithoutLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void setSettingsWithoutCSRF() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/settings")
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void setSettingsWrongFirstnameTooSmall() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList("F"));
            put("lastname", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", String.format(ServerErrors.WRONG_FIRSTNAME,
                        validator.getMinNameLength(), validator.getMaxNameLength())));
        User afterPostUser = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertEquals(vdk64.getFirstname(), afterPostUser.getFirstname());
    }

    @Test
    public void setSettingsWrongFirstnameTooLarge() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList("Firstnameofusertoomuch"));
            put("lastname", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", String.format(ServerErrors.WRONG_FIRSTNAME,
                        validator.getMinNameLength(), validator.getMaxNameLength())));
        User afterPostUser = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertEquals(vdk64.getFirstname(), afterPostUser.getFirstname());
    }

    @Test
    public void setSettingsLastnameToSmall() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList("L"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", String.format(ServerErrors.WRONG_LASTNAME,
                        validator.getMinNameLength(), validator.getMaxNameLength())));
        User afterPostUser = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertEquals(vdk64.getFirstname(), afterPostUser.getFirstname());
    }

    @Test
    public void setSettingsLastnameToLarge() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList("Lastnameistoolargetoconfirm"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", String.format(ServerErrors.WRONG_LASTNAME,
                        validator.getMinNameLength(), validator.getMaxNameLength())));
        User afterPostUser = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertEquals(vdk64.getFirstname(), afterPostUser.getFirstname());
    }

    @Test
    public void setSettingsWithoutChanges() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attributeExists("ok"));
        User updateUser = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertEquals(vdk64.getFirstname(), updateUser.getFirstname());
        assertEquals(vdk64.getLastname(), updateUser.getLastname());
    }

    @Test
    public void setSettingsFirstname() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList("Firstname"));
            put("lastname", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attributeExists("ok"));
        User updateUser = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertEquals("Firstname", updateUser.getFirstname());
        userRepo.save(vdk64);
    }

    @Test
    public void setSettingsLastname() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList(""));
            put("lastname", Collections.singletonList("Lastname"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attributeExists("ok"));
        User updateUser = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertEquals("Lastname", updateUser.getLastname());
        userRepo.save(vdk64);
    }

    @Test
    public void setSettingsFirstnameAndLastname() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("changeData", Collections.singletonList(""));
            put("firstname", Collections.singletonList("Firstname"));
            put("lastname", Collections.singletonList("Lastname"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params)
                .with(user(vdk64)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attributeExists("ok"));
        User updateUser = userRepo.findByUsername(vdk64.getUsername()).orElseThrow();
        assertEquals("Lastname", updateUser.getLastname());
        assertEquals("Firstname", updateUser.getFirstname());
        userRepo.save(vdk64);
    }

    @Test
    public void testDepositWithoutLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("deposit", Collections.singletonList(""));
            put("value", Collections.singletonList("100"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testDepositWithoutCSRF() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("deposit", Collections.singletonList(""));
            put("value", Collections.singletonList("100"));
        }};

        mockMvc.perform(post("/settings")
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDepositEmptyField() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("deposit", Collections.singletonList(""));
            put("value", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", ServerErrors.WRONG_VALUE));
    }

    @Test
    public void testDepositWithWrongValues() {
        List<String> values = new ArrayList<>(Arrays.asList("150/", "150*", "150/*",
                "150/5", "150*5", "/", "*", "!", "@", "#", "$", "%", "^", "&", "?", "15asd", "@51123@?"));

        values.forEach(value -> doPostAndCheckDepositChanges(value, "deposit"));
        assertEquals(userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getDeposit(),
                vdk64.getDeposit());
    }

    @Test
    public void testDepositWithDot() throws Exception {
        Float deposit = vdk64.getDeposit();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("deposit", Collections.singletonList(""));
            put("value", Collections.singletonList("100.50"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attribute("ok", "Your deposit was successfully replenished!"));
        assertEquals(userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getDeposit(),
                deposit + 100.5F, 0.0);
        userRepo.save(vdk64);
    }

    @Test
    public void testDepositWithComma() throws Exception {
        Float deposit = vdk64.getDeposit();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("deposit", Collections.singletonList(""));
            put("value", Collections.singletonList("100,50"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attribute("ok", "Your deposit was successfully replenished!"));
        assertEquals(userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getDeposit(),
                deposit + 100.5F, 0.0);
        userRepo.save(vdk64);
    }

    @Test
    public void testDeposit() throws Exception {
        Float deposit = vdk64.getDeposit();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("deposit", Collections.singletonList(""));
            put("value", Collections.singletonList("100"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attribute("ok", "Your deposit was successfully replenished!"));
        assertEquals(userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getDeposit(),
                deposit + 100F, 0.0);
        userRepo.save(vdk64);
    }

    @Test
    public void testWithdrawWithoutLogin() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("withdraw", Collections.singletonList(""));
            put("value", Collections.singletonList("100"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .params(params))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void testWithdrawWithoutCSR() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("withdraw", Collections.singletonList(""));
            put("value", Collections.singletonList("100"));
        }};

        mockMvc.perform(post("/settings")
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testWithdrawEmptyField() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("withdraw", Collections.singletonList(""));
            put("value", Collections.singletonList(""));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", ServerErrors.WRONG_VALUE));
    }

    @Test
    public void testWithdrawWithWrongValues() {
        List<String> values = new ArrayList<>(Arrays.asList("150/", "150*", "150/*",
                "150/5", "150*5", "/", "*", "!", "@", "#", "$", "%", "^", "&", "?", "15asd", "@51123@?"));

        values.forEach(value -> doPostAndCheckDepositChanges(value, "withdraw"));
        assertEquals(userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getDeposit(),
                vdk64.getDeposit());
    }

    @Test
    public void testWithdrawMoreThanDeposit() throws Exception {
        Float deposit = vdk64.getDeposit();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("withdraw", Collections.singletonList(""));
            put("value", Collections.singletonList((String.valueOf(deposit + 0.1F))));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("admin"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attribute("error", ServerErrors.NOT_ENOUGH_DEPOSIT_TO_TRANSACTION));
    }

    @Test
    public void testWithdrawWithDot() throws Exception {
        Float deposit = vdk64.getDeposit();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("withdraw", Collections.singletonList(""));
            put("value", Collections.singletonList("999.50"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attribute("ok", "Withdraw was successfully!"));
        assertEquals(userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getDeposit(),
                deposit - 999.50F, 0.0);
        userRepo.save(vdk64);
    }

    @Test
    public void testWithdrawWithComma() throws Exception {
        Float deposit = vdk64.getDeposit();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("withdraw", Collections.singletonList(""));
            put("value", Collections.singletonList("999,50"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attribute("ok", "Withdraw was successfully!"));
        assertEquals(userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getDeposit(),
                deposit - 999.50F, 0.0);
        userRepo.save(vdk64);
    }

    @Test
    public void testWithdraw() throws Exception {
        Float deposit = vdk64.getDeposit();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            put("withdraw", Collections.singletonList(""));
            put("value", Collections.singletonList("1000"));
        }};

        mockMvc.perform(post("/settings")
                .with(csrf())
                .with(user(vdk64))
                .params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newBets"))
                .andExpect(model().attributeExists("newMessages"))
                .andExpect(model().attribute("admin", true))
                .andExpect(model().attribute("ok", "Withdraw was successfully!"));
        assertEquals(userRepo.findByUsername(vdk64.getUsername()).orElseThrow().getDeposit(),
                deposit - 1000F, 0.0);
        userRepo.save(vdk64);
    }

    private boolean deleteFileAndDirectory(User user) {
        boolean deleteFile = new File(uploadPath + "/" + user.getId() + "/",
                user.getAvatar()).delete();
        boolean deleteDirectory = new File(uploadPath + "/" + user.getId()).delete();
        return deleteFile && deleteDirectory;
    }

    private void createFileAndSetAvatar(User principal) throws Exception {
        MockMultipartFile myFile = new MockMultipartFile("file", "myFile",
                "multipart/form-data", new byte[5]);
        mockMvc.perform(multipart("/settings")
                .file(myFile)
                .with(csrf())
                .with(user(principal)))
                .andDo(print());
    }

    private void doPostAndCheckDepositChanges(String value, String method) {
        try {
            mockMvc.perform(post("/settings")
                    .with(csrf())
                    .with(user(vdk64))
                    .param(method, "")
                    .param("value", value))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("admin"))
                    .andExpect(model().attributeExists("newMessages"))
                    .andExpect(model().attributeExists("newBets"))
                    .andExpect(model().attribute("error", ServerErrors.WRONG_VALUE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}