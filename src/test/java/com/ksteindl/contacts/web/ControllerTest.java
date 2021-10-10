package com.ksteindl.contacts.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksteindl.contacts.BaseTests;
import com.ksteindl.contacts.TestUtils;
import com.ksteindl.contacts.domain.Status;
import com.ksteindl.contacts.domain.entities.Contact;
import com.ksteindl.contacts.domain.repository.AppUserRepository;
import com.ksteindl.contacts.domain.repository.ContactRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ControllerTest extends BaseTests {

    private static final Logger logger = LogManager.getLogger(ControllerTest.class);

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

    //UPDATE CONTACT
    @Test
    @Rollback
    @Transactional
    void testUpdateContact_whenCorrectInput_got201() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getUpdateContactInput();
        //Doesn't matter, which contect will be updated, plus rollback will be occured
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenNotLoggedIn_got401() throws Exception {
        ContactInput input = TestUtils.getUpdateContactInput();
        //Doesn't matter, which contect will be updated, plus rollback will be occured
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(401))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testUpdateContact_whenIdDoesNotExists_got404() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getUpdateContactInput();
        //Doesn't matter, which contect will be updated, plus rollback will be occured
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + Integer.MAX_VALUE)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }


    @Test
    @Rollback
    @Transactional
    void testUpdateContact_whenCorrectInput_gotCorrectResponse() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getUpdateContactInput();
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenCorrectInput_storedDataIsCorrect(@Autowired ContactService contactService) throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getUpdateContactInput();
        Integer id = 1;
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(201))
                .andReturn();
        JSONObject contactJSONObject = new JSONObject(result.getResponse().getContentAsString());
        Contact stored = contactService.findContactById((long)id);
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
    void testUpdareContact_whenFirstNameEmpty_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setFirstName("");
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenLastNameEmpty_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setLastName("");
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenEmailEmpty_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setEmail("");
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenEmailWrongFormat2_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setEmail("hello.world");
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenCompanyIdMissing_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setCompanyId(null);
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenCompanyDoesNotExist_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setCompanyId((long)Integer.MAX_VALUE);
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenPhoneEmpty1_got201() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setPhone("");
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenPhoneEmpty2_got201() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setPhone(null);
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenPhoneWrongFormat1_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setPhone("123456789");
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenPhoneNotE164_got400() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        //Valid format, but not E164
        input.setPhone("06201234567");
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenCommentNull_got201() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setComment(null);
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
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
    void testUpdateContact_whenStatusNotValid_got401() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        ContactInput input = TestUtils.getTestContactInput();
        input.setStatus("non-valid");
        MvcResult result = mvc.perform(put(CONTACT_URL + "/" + 1)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(input)))
                .andExpect(status().is(400))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    //DELETE CONTACT
    @Test
    @Rollback
    @Transactional
    void testDeleteContact_whenCorrect_got201() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        //Doesn't matter, which contect will be updated, plus rollback will be occured
        MvcResult result = mvc.perform(delete(CONTACT_URL + "/" + 1)
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteContact_whenNotLoggedIn_got401() throws Exception {
        //Doesn't matter, which contect will be updated, plus rollback will be occured
        MvcResult result = mvc.perform(delete(CONTACT_URL + "/" + 1))
                .andExpect(status().is(401))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteContact_whenIdDoesNotExists_got404() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        //Doesn't matter, which contect will be updated, plus rollback will be occured
        MvcResult result = mvc.perform(delete(CONTACT_URL + "/" + Integer.MAX_VALUE)
                        .header("Authorization", token))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteContact_whenCorrect_gotStatusIsDeleted() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        //Doesn't matter, which contect will be updated, plus rollback will be occured
        MvcResult result = mvc.perform(delete(CONTACT_URL + "/" + 1)
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is(Status.DELETED.name())))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    @Rollback
    @Transactional
    void testDeleteContact_whenCorrect_storedDataIsDeleted(@Autowired ContactService contactService) throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        //Doesn't matter, which contect will be updated, plus rollback will be occured
        Integer id = 1;
        MvcResult result = mvc.perform(delete(CONTACT_URL + "/" + id)
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.status", is(Status.DELETED.name())))
                .andReturn();
        Contact stored = contactService.findContactById((long)id);
        Assertions.assertEquals(Status.DELETED.name(), stored.getStatus().name());
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    //FIND BY ID CONTACT

    @Test
    void testFindContactById_whenCorrect_got201(@Autowired ContactRepository contactRepository) throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        //Contact stored = contactRepository.findAll().stream().filter(contact -> contact.getFirstName().equals(TestUtils.CONTACT_PRESTORED_INPUT_FIRSTNAME)).findAny().get();
        Contact stored = contactRepository.findById(1l).get();
        MvcResult result = mvc.perform(get(CONTACT_URL + "/" + stored.getId())
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testFindContactById_whenNotLoggedIn_got401(@Autowired ContactRepository contactRepository) throws Exception {
        //Contact stored = contactRepository.findAll().stream().filter(contact -> contact.getFirstName().equals(TestUtils.CONTACT_PRESTORED_INPUT_FIRSTNAME)).findAny().get();
        Contact stored = contactRepository.findById(1l).get();
        MvcResult result = mvc.perform(get(CONTACT_URL + "/" + stored.getId()))
                .andExpect(status().is(401))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testFindContactById_whenDoesNotExists_got404(@Autowired ContactRepository contactRepository) throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "/" + Integer.MAX_VALUE)
                        .header("Authorization", token))
                .andExpect(status().is(404))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testFindContactById_whenCorrectInput_gotCorrectResponse(@Autowired ContactRepository contactRepository) throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        //Contact stored = contactRepository.findAll().stream().filter(contact -> contact.getFirstName().equals(TestUtils.CONTACT_PRESTORED_INPUT_FIRSTNAME)).findAny().get();
        Contact stored = contactRepository.findById(1l).get();
        MvcResult result = mvc.perform(get(CONTACT_URL + "/" + stored.getId())
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email", is(stored.getEmail())))
                .andExpect(jsonPath("$.firstName", is(stored.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(stored.getLastName())))
                .andExpect(jsonPath("$.company.id", is(stored.getCompany().getId().intValue())))
                .andExpect(jsonPath("$.comment", is(stored.getComment())))
                .andExpect(jsonPath("$.status", is(stored.getStatus().name())))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testFindContactById_whenCorrectInput_storedDataIsCorrect(@Autowired ContactRepository contactRepository) throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        //Contact stored = contactRepository.findAll().stream().filter(contact -> contact.getFirstName().equals(TestUtils.CONTACT_PRESTORED_INPUT_FIRSTNAME)).findAny().get();
        Contact stored = contactRepository.findById(1l).get();
        MvcResult result = mvc.perform(get(CONTACT_URL + "/1" + stored.getId())
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    //QUERY CONTACTS
    @Test
    void testQueryContacts_withDefault_got200() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL)
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenNotLoggedIn_got401() throws Exception {
        MvcResult result = mvc.perform(get(CONTACT_URL))
                .andExpect(status().is(401))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_withDefault_gotGotRightStructure() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL)
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalItems").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.currentPage").isNumber())
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_withDefault_gotGotRightPaging() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL)
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalItems", is(24)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenSecondPage_gotGotRightPaging() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?page=1")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalItems", is(24)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.currentPage", is(1)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenSecondPage_gotGotCorrectData() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?page=1")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content[0].firstName", is("Z")))
                .andExpect(jsonPath("$.content[9].firstName", is("Z")))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenThirdPage_gotGotCorrectData() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?page=2")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content[0].firstName", is("Z")))
                .andExpect(jsonPath("$.content[3].firstName", is("Z")))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenDefault_gotSortedByName() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL)
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content[0].lastName", is("Balogh")))
                .andExpect(jsonPath("$.content[1].lastName", is("Donath")))
                .andExpect(jsonPath("$.content[2].lastName", is("Donath")))
                .andExpect(jsonPath("$.content[3].lastName", is("Takacs")))
                .andExpect(jsonPath("$.content[1].firstName", is("Andras")))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenSortByPhone_gotSortedByPhone() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?sortBy=phone")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content[0].phone", is("+14155552671")))
                .andExpect(jsonPath("$.content[1].phone", is("+360000001")))
                .andExpect(jsonPath("$.content[2].phone", is("+360000002")))
                .andExpect(jsonPath("$.content[3].phone", is("+360000004")))
                .andExpect(jsonPath("$.content[4].phone", is("+3699999999")))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenSortByEmail_gotSortedByEmail() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?sortBy=email")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content[0].email", is("b.donath@alpha.com")))
                .andExpect(jsonPath("$.content[1].email", is("donath.a@beta.com")))
                .andExpect(jsonPath("$.content[2].email", is("uzeteny.balogh@donath.hu")))
                .andExpect(jsonPath("$.content[3].email", is("uzeteny.takacs@takacs.hu")))
                .andExpect(jsonPath("$.content[4].email", is("z.z@z.com")))

                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenSortByCompany_gotSortedByCompany() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?sortBy=company")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content[0].company.name", is(TestUtils.ALPHA_COMPANY)))
                .andExpect(jsonPath("$.content[1].company.name", is(TestUtils.BETA_COMPANY)))
                .andExpect(jsonPath("$.content[2].company.name", is(TestUtils.BETA_COMPANY)))
                .andExpect(jsonPath("$.content[3].company.name", is(TestUtils.GAMMA_COMPANY)))
                .andExpect(jsonPath("$.content[4].company.name", is(TestUtils.GAMMA_COMPANY)))

                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenQueryByZeteny_gotTwoHits() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?queryString=Zeteny")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content[0].firstName", is("Zeteny")))
                .andExpect(jsonPath("$.content[1].firstName", is("Zeteny")))
                .andExpect(jsonPath("$.totalItems", is(2)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenQueryByOnath_gotThreeHits() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?queryString=onath")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content[*].lastName", hasItem("Donath")))
                .andExpect(jsonPath("$.content[*].email", hasItem("uzeteny.balogh@donath.hu")))
                .andExpect(jsonPath("$.totalItems", is(3)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenQueryByPhone_gotThreeHits() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?queryString=000000")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.totalItems", is(3)))
                .andReturn();
        logger.info("status code: " + result.getResponse().getStatus());
        logger.info("response content:\n" + result.getResponse().getContentAsString());
    }

    @Test
    void testQueryContacts_whenQueryByDummy_gotZeroHit() throws Exception {
        String token = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
        MvcResult result = mvc.perform(get(CONTACT_URL + "?queryString=dummy")
                        .header("Authorization", token))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.totalItems", is(0)))
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
