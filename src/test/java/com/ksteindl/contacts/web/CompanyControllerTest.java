package com.ksteindl.contacts.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.contacts.BaseTests;
import com.ksteindl.contacts.TestUtils;
import com.ksteindl.contacts.domain.entities.Contact;
import com.ksteindl.contacts.domain.repository.AppUserRepository;
import com.ksteindl.contacts.security.JwtProvider;
import com.ksteindl.contacts.service.ContactService;
import com.ksteindl.contacts.web.input.AppUserInput;
import com.ksteindl.contacts.web.input.ContactInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CompanyControllerTest extends BaseTests {

    private static final Logger logger = LogManager.getLogger(CompanyControllerTest.class);

    private static final String CONTACT_URL = "/api/contact";
    private static final String COMPANY_URL = "/api/company";
    private static final String USER_URL = "/api/users";

    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    private MockMvc mvc;

    //CREATE CONTACT
    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenCorrectInput_got201() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenNotLoggedIn_got401() throws Exception {
        ContactInput input = TestUtils.getTestContactInput();
        mvc.perform(post(CONTACT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(401));
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenCorrectInput_gotCorrectResponse() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email", is(input.getEmail())))
                .andExpect(jsonPath("$.firstName", is(input.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(input.getLastName())))
                .andExpect(jsonPath("$.company.id", is(input.getCompanyId().intValue())))
                .andExpect(jsonPath("$.comment", is(input.getComment())))
                .andExpect(jsonPath("$.status", is(input.getStatus())))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenCorrectInput_storedDataIsCorrect(@Autowired ContactService contactService) throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        JSONObject contactJSONObject = new JSONObject(result.getResponse().getContentAsString());
        Long id = contactJSONObject.getLong("id");
        Contact stored = contactService.findContactById(id);
        Assertions.assertEquals(input.getEmail(), stored.getEmail());
        Assertions.assertEquals(input.getFirstName(), stored.getFirstName());
        Assertions.assertEquals(input.getLastName(), stored.getLastName());
        Assertions.assertEquals(input.getCompanyId().intValue(),  stored.getCompany().getId());
        Assertions.assertEquals(input.getComment(), stored.getComment());
        Assertions.assertEquals(input.getStatus(), stored.getStatus().name());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenFirstNameEmpty_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setFirstName("");
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenLastNameEmpty_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setLastName("");
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenEmailEmpty_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setEmail("");
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenEmailWrongFormat2_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setEmail("hello.world");
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenCompanyIdMissing_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setCompanyId(null);
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Rollback
    @Transactional
    void testCreateContact_whenCompanyDoesNotExist_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setCompanyId((long)Integer.MAX_VALUE);
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenPhoneEmpty1_got201() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setPhone("");
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenPhoneEmpty2_got201() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setPhone(null);
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenPhoneWrongFormat1_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setPhone("123456789");
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenPhoneNotE164_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        //Valid format, but not E164
        input.setPhone("06201234567");
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenCommentNull_got201() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setComment(null);
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testCreateContact_whenStatusNotValid_got401() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setStatus("non-valid");
        MvcResult result = mvc.perform(post(CONTACT_URL)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }



    //COMPANY AND APPUSER CONTROLLER TESTS
    @Test
    @Transactional
    void testGetCompanies_whenAuthorized_got3Company() throws Exception {
        logger.info(appUserRepository.findByUsername(TestUtils.ADMIN_USERNAME).get().toString());
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        logger.info(token);
        MvcResult result = mvc.perform(get(COMPANY_URL)
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(3)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
    }

    @Test
    @Transactional
    @Rollback
    void testCreateUser_whenCorrectInput_got200() throws Exception {
        AppUserInput adminInput = TestUtils.getAdmin2Input();
        mvc.perform(post(USER_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(adminInput)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.username", is(adminInput.getUsername())));
    }

    @Test
    @Transactional
    @Rollback
    void testCreateUser_whenUsernameAlreadyTaken_got400() throws Exception {
        AppUserInput adminInput = TestUtils.getAdminInput();
        mvc.perform(post(USER_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(adminInput)))
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    @Rollback
    void testLoginUser_withIncorrectPassword_got401() throws Exception {
        AppUserInput adminInput = TestUtils.getAdminInput();
        adminInput.setPassword("wrong-password");
        mvc.perform(post(USER_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(adminInput)))
                .andExpect(status().is(401));
    }

    @Test
    @Transactional
    @Rollback
    void testLoginUser_withCorrectCredentials_gotToken() throws Exception {
        AppUserInput adminInput = TestUtils.getAdminInput();
        mvc.perform(post(USER_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(adminInput)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.token").isString());
    }

    public String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
