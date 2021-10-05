package com.ksteindl.contacts.web;

import com.ksteindl.contacts.domain.entities.Contact;
import com.ksteindl.contacts.domain.input.ContactInput;
import com.ksteindl.contacts.service.ContactService;
import com.ksteindl.contacts.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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

    @DeleteMapping
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteContact(@PathVariable Long id) {
        logger.info("DELETE '/contact' was called with id {}",id);
        contactService.deleteContact(id);
        logger.info("DELETE '/contact' was succesful for id {}", id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable Long id) {
        logger.info("GET '/contact' was called with id {}",id);
        Contact contact = contactService.findContactById(id);
        logger.info("GET '/contact' was returned with {}", contact);
        return ResponseEntity.status(200).body(contact);
    }

    @GetMapping
    public ResponseEntity<PagedList> getActiveContacts(
            @RequestParam(value="sortBy", required = false) String sortBy,
            @RequestParam(value="queryString", required = false) String queryString,
            @RequestParam(value="page", required = false) Integer page,
            @RequestParam(value= "size", required = false) Integer size
    ) {
        logger.info("GET '/contact' was called with parameters:" +
                "sortBy=" + sortBy +
                ", queryString=" + queryString +
                ", page=" + page +
                ", size=" + size);
        ContactQueryRequest request = ContactQueryRequest.builder()
                .sortBy(sortBy)
                .queryString(queryString)
                .page(page)
                .size(size).build();
        PagedList contacts = contactService.findContacts(request);
        logger.info("GET '/contact' was returned with a list of {} contacts", contacts.getContent().size());
        return ResponseEntity.status(200).body(contacts);
    }


    //This method should be moved out to a separate @Service, when multiple Controller will use
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
