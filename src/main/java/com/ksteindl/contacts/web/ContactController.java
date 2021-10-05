package com.ksteindl.contacts.web;

import com.ksteindl.contacts.domain.entities.Contact;
import com.ksteindl.contacts.domain.input.ContactInput;
import com.ksteindl.contacts.service.ContactService;
import com.ksteindl.contacts.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("api/contact")
public class ContactController {

    Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @PostMapping
    public ResponseEntity<Contact> createContact(
            @RequestBody @Valid ContactInput contactInput,
            BindingResult result) {
        logger.info("POST '/contact' was called with {}", contactInput);
        throwExceptionIfNotValid(result);
        Contact contact = contactService.createContact(contactInput);
        logger.info("POST '/contact' was returned with {}", contact);
        return ResponseEntity.status(200).body(contact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(
            @RequestBody @Valid ContactInput contactInput,
            @PathVariable Long id,
            BindingResult result) {
        logger.info("PUT '/contact' was called with id {} and body {}",id, contactInput);
        throwExceptionIfNotValid(result);
        Contact contact = contactService.updateContact(id, contactInput);
        logger.info("PUT '/contact' was returned with {}", contact);
        return ResponseEntity.status(200).body(contact);
    }

    @GetMapping
    public ResponseEntity<String> getWelcome() {
        return ResponseEntity.status(200).body("hello world");
    }

    // This method could be moved to a separate @Service, if multiple Controller used
    private void throwExceptionIfNotValid(BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = result.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (errorMessage1, errorMessage2) -> errorMessage1 + ", " + errorMessage2));
            logger.info("ValidationException is being thrown with errorMap: {0}", errorMap);
            throw new ValidationException(errorMap);
        }
    }



}
