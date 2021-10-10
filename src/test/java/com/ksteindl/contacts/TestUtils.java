package com.ksteindl.contacts;

import com.ksteindl.contacts.domain.Status;
import com.ksteindl.contacts.web.input.AppUserInput;
import com.ksteindl.contacts.web.input.ContactInput;

public class TestUtils {

    public static final Long CONTACT_TEST_INPUT_COMPANY_ID = 1l;
    public static final String CONTACT_TEST_INPUT_COMMENT = "hello";
    public static final String CONTACT_TEST_INPUT_EMAIL = "test.input@company.com";
    public static final String CONTACT_TEST_INPUT_FIRSTNAME = "Test";
    public static final String CONTACT_TEST_INPUT_LASTNAME = "Input";
    public static final String CONTACT_TEST_INPUT_PHONE = "+3692345678";
    public static ContactInput getTestContactInput() {
       ContactInput contactInput = new ContactInput();
       contactInput.setCompanyId(CONTACT_TEST_INPUT_COMPANY_ID);
       contactInput.setComment(CONTACT_TEST_INPUT_COMMENT);
       contactInput.setEmail(CONTACT_TEST_INPUT_EMAIL);
       contactInput.setFirstName(CONTACT_TEST_INPUT_FIRSTNAME);
       contactInput.setLastName(CONTACT_TEST_INPUT_LASTNAME);
       contactInput.setPhone(CONTACT_TEST_INPUT_PHONE);
       contactInput.setStatus(Status.ACTIVE.name());
       return contactInput;
    }

    public static final Long CONTACT_UPDATE_INPUT_COMPANY_ID = 1l;
    public static final String CONTACT_UPDATE_INPUT_COMMENT = "update";
    public static final String CONTACT_UPDATE_INPUT_EMAIL = "update.input@company.com";
    public static final String CONTACT_UPDATE_INPUT_FIRSTNAME = "Update";
    public static final String CONTACT_UPDATE_INPUT_LASTNAME = "Input";
    public static final String CONTACT_UPDATE_INPUT_PHONE = "+3682445678";
    public static ContactInput getUpdateContactInput() {
        ContactInput contactInput = new ContactInput();
        contactInput.setCompanyId(CONTACT_UPDATE_INPUT_COMPANY_ID);
        contactInput.setComment(CONTACT_UPDATE_INPUT_COMMENT);
        contactInput.setEmail(CONTACT_UPDATE_INPUT_EMAIL);
        contactInput.setFirstName(CONTACT_UPDATE_INPUT_FIRSTNAME);
        contactInput.setLastName(CONTACT_UPDATE_INPUT_LASTNAME);
        contactInput.setPhone(CONTACT_UPDATE_INPUT_PHONE);
        contactInput.setStatus(Status.ACTIVE.name());
        return contactInput;
    }

//    public static final Long CONTACT_PRESTORED_INPUT_COMPANY_ID = 2l;
//    public static final String CONTACT_PRESTORED_INPUT_COMMENT = "prestored";
//    public static final String CONTACT_PRESTORED_INPUT_EMAIL = "prestored.input@company.com";
//    public static final String CONTACT_PRESTORED_INPUT_FIRSTNAME = "Input";
//    public static final String CONTACT_PRESTORED_INPUT_LASTNAME = "Prestored";
//    public static final String CONTACT_PRESTORED_INPUT_PHONE = "+3672445678";
//    public static ContactInput getPrestoredContactInput() {
//        ContactInput contactInput = new ContactInput();
//        contactInput.setCompanyId(CONTACT_PRESTORED_INPUT_COMPANY_ID);
//        contactInput.setComment(CONTACT_PRESTORED_INPUT_COMMENT);
//        contactInput.setEmail(CONTACT_PRESTORED_INPUT_EMAIL);
//        contactInput.setFirstName(CONTACT_PRESTORED_INPUT_FIRSTNAME);
//        contactInput.setLastName(CONTACT_PRESTORED_INPUT_LASTNAME);
//        contactInput.setPhone(CONTACT_PRESTORED_INPUT_PHONE);
//        contactInput.setStatus(Status.ACTIVE.name());
//        return contactInput;
//    }

    public static final String ALPHA_COMPANY = "Alpha Company";
    public static final String BETA_COMPANY = "Beta Company";
    public static final String GAMMA_COMPANY = "Gamma Company";

    public static final String ADMIN_PASSWORD = "admin";
    public static final String ADMIN_USERNAME = "admin";
    public static AppUserInput getAdminInput() {
        AppUserInput adminInput = new AppUserInput();
        adminInput.setUsername(ADMIN_USERNAME);
        adminInput.setPassword(ADMIN_PASSWORD);
        return adminInput;
    }

    public static final String ADMIN2_USERNAME = "admin2";
    public static AppUserInput getAdmin2Input() {
        AppUserInput adminInput = new AppUserInput();
        adminInput.setUsername(ADMIN2_USERNAME);
        adminInput.setPassword(ADMIN_PASSWORD);
        return adminInput;
    }



}
