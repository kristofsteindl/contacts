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
import com.ksteindl.contacts.web.ContactQueryRequest;
import com.ksteindl.contacts.web.PagedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Set;

@Service
public class ContactService {

    Logger logger = LoggerFactory.getLogger(ContactService.class);

    public static final Sort DEFAULT_SORT_BY_NAME = Sort.by("lastName").ascending().and(Sort.by("firstName")).ascending();
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer DEFAULT_PAGE = 0;

    public static final String COMPANY_PARAM = "company";
    public static final Set<String> VALID_SORT_PARAMS =Set.of("email", "phone");

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private MailSender mailSender;

    public Iterable<Company> getCompanies() {
        return companyRepository.findAll();
    }


    public Contact createContact(ContactInput contactInput) {
        Contact contact = new Contact();
        Contact persisted = createOrUpdateContact(contact, contactInput);
        sendWelcomeMessage(contact);
        return persisted;
    }

    public Contact updateContact(Long id, ContactInput contactInput) {
        Contact contact = findContactById(id);
        return createOrUpdateContact(contact, contactInput);
    }

    public void deleteContact(Long id) {
        Contact contact = findContactById(id);
        contactRepository.delete(contact);
    }

    public Contact findContactById(Long id) {
        return contactRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("contact", id));
    }


    //The specification mentions 4 attributes to visualize, but the whole Contact object is returned because of simplicity.
    //Visualization of the required attributes is FE responsibility.
    @Transactional
    public PagedList findContacts(ContactQueryRequest request) {
        Sort sortBy = getSortBy(request.getSortBy());
        Integer size = request.size;
        Integer page = request.page;
        String queryString = request.queryString;
        Pageable paging = PageRequest.of(
                page == null || page < 1 ? DEFAULT_PAGE : page,
                size == null || size < 1 ? DEFAULT_PAGE_SIZE : size,
                sortBy);
        Page<Contact> contactPage;
        if (queryString == null || queryString.equals("")) {
            contactPage = contactRepository.findContactsByStatus(Status.ACTIVE, paging);
        } else {
            contactPage = contactRepository.findContactsByQueryString(queryString, paging);
        }
        return new PagedList(contactPage);

    }

    public Company findCompanyById(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("company", id));
    }

    private Sort getSortBy(String stringSortBy) {
        if (stringSortBy == null || stringSortBy.equals("")) {
            return DEFAULT_SORT_BY_NAME;
        } else if (stringSortBy.equals(COMPANY_PARAM)){
            return Sort.by(Sort.Direction.ASC, "company.name");
        } else if (VALID_SORT_PARAMS.contains(stringSortBy)) {
            return Sort.by(Sort.Direction.ASC, stringSortBy);
        }
        throw new ValidationException(String.format("Sorting parameter must be only 'company', 'name' or among these: %s", VALID_SORT_PARAMS));
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

    private void sendWelcomeMessage(Contact contact) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@contacts.com");
        message.setTo(contact.getEmail());
        message.setSubject("welcome");
        message.setText(String.format("Üdv, %s!", contact.getFirstName()));
        mailSender.send(message);
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
