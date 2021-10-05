package com.ksteindl.contacts.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import com.google.i18n.phonenumbers.Phonenumber.*;
import com.ksteindl.contacts.domain.Status;
import com.ksteindl.contacts.domain.entities.Company;
import com.ksteindl.contacts.domain.entities.Contact;
import com.ksteindl.contacts.domain.input.ContactInput;
import com.ksteindl.contacts.domain.repository.CompanyRepository;
import com.ksteindl.contacts.domain.repository.ContactRepository;
import com.ksteindl.contacts.exception.ResourceNotFoundException;
import com.ksteindl.contacts.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ContactService {

    Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ContactRepository contactRepository;

    public Iterable<Company> getCompanies() {
        return companyRepository.findAll();
    }


    public Contact createContact(ContactInput contactInput) {
        Contact contact = new Contact();
        return createOrUpdateContact(contact, contactInput);
    }

    public Contact updateContact(Long id, ContactInput contactInput) {
        Contact contact = findContactById(id);
        return createOrUpdateContact(contact, contactInput);
    }

    public Contact findContactById(Long id) {
        return contactRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("contact", id));
    }

    public Company findCompanyById(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("company", id));
    }

    private Contact createOrUpdateContact(Contact contact, ContactInput contactInput) {
        Company company = findCompanyById(contactInput.getCompanyId());
        String phone = validateE164AndGetPhoneNumber(contactInput.getPhone());
        Status status = validateStatusAndGet(contactInput.getStatus());

        contact.setFirstName(contactInput.getFirstName());
        contact.setLastName(contactInput.getLastName());
        contact.setEmail(contactInput.getEmail());
        contact.setComment(contactInput.getComment());
        contact.setCompany(company);
        contact.setPhone(phone);
        contact.setStatus(status);
        return contactRepository.save(contact);
    }

    private Status validateStatusAndGet(String stringStatus) {
        try {
            return Status.valueOf(stringStatus);
        } catch (IllegalArgumentException exception) {
            throw new ValidationException(String.format("%s is not valid status, must be one of the following: %s", stringStatus, Arrays.asList(Status.values()).toString()));
        }

    }

    private String validateE164AndGetPhoneNumber(String stringPhone) {
        String exceptionTemplate = "%s is unknown phone number format";
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            PhoneNumber phone = phoneNumberUtil.parse(
                    stringPhone,
                    PhoneNumber.CountryCodeSource.UNSPECIFIED.name());

            if (!phoneNumberUtil.isValidNumber(phone)) {
                logger.info("Phone number is not valid");
                throw new ValidationException(String.format(exceptionTemplate, stringPhone));
            }
            String i164Number = phoneNumberUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164);
            if (!i164Number.equals(stringPhone)) {
                logger.info("Phone number is not in i164 format");
                throw new ValidationException(String.format("%s is not in E164 phone number format", stringPhone));
            }
            return i164Number;
        } catch (NumberParseException exception) {
            logger.info("Phone number cannot be parsed, NumberParseException was thrown.");
            throw new ValidationException(String.format(exceptionTemplate, stringPhone));
        }
    }

}
