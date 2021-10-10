package com.ksteindl.contacts;


import com.ksteindl.contacts.domain.Status;
import com.ksteindl.contacts.domain.entities.Company;
import com.ksteindl.contacts.domain.entities.Contact;
import com.ksteindl.contacts.domain.repository.CompanyRepository;
import com.ksteindl.contacts.domain.repository.ContactRepository;
import com.ksteindl.contacts.security.JwtProvider;
import com.ksteindl.contacts.service.AppUserService;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseTests {

    private static boolean first = true;

    public static String TOKEN_FOR_ADMIN;

    @Autowired
    protected JwtProvider jwtProvider;

    @BeforeAll
    static void initDb(
            @Autowired AppUserService appUserService,
            @Autowired CompanyRepository companyRepository,
            @Autowired ContactRepository contactRepository,
            @Autowired JwtProvider jwtProvider) throws CloneNotSupportedException {
        if (first) {
            appUserService.saveUser(TestUtils.getAdminInput());
            initCompanies(companyRepository);
            //contactService.createContact(TestUtils.getPrestoredContactInput());
            initContacts(contactRepository, companyRepository);
            TOKEN_FOR_ADMIN = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
            first = false;
        }
    }

    private static void initContacts(ContactRepository contactRepository, CompanyRepository companyRepository) throws CloneNotSupportedException {
        Company alphaCompany = companyRepository.findByName(TestUtils.ALPHA_COMPANY).get();
        Company betaCompany = companyRepository.findByName(TestUtils.BETA_COMPANY).get();
        Company gammaCompany = companyRepository.findByName(TestUtils.GAMMA_COMPANY).get();

        Contact contact1 = new Contact();
        contact1.setFirstName("Andras");
        contact1.setLastName("Donath");
        contact1.setCompany(betaCompany);
        contact1.setEmail("donath.a@beta.com");
        contact1.setComment("aaa");
        contact1.setPhone("+360000002");
        contact1.setStatus(Status.ACTIVE);
        contactRepository.save(contact1);

        Contact contact2 = new Contact();
        contact2.setFirstName("Bertold");
        contact2.setLastName("Donath");
        contact2.setCompany(alphaCompany);
        contact2.setEmail("b.donath@alpha.com");
        contact2.setComment("zzz");
        contact2.setPhone("+360000004");
        contact2.setStatus(Status.ACTIVE);
        contactRepository.save(contact2);

        Contact contact3 = new Contact();
        contact3.setFirstName("Zeteny");
        contact3.setLastName("Balogh");
        contact3.setCompany(gammaCompany);
        contact3.setEmail("uzeteny.balogh@donath.hu");
        contact3.setComment("Takacs");
        contact3.setPhone("+360000001");
        contact3.setStatus(Status.ACTIVE);
        contactRepository.save(contact3);

        Contact contact4 = new Contact();
        contact4.setFirstName("Zeteny");
        contact4.setLastName("Takacs");
        contact4.setCompany(betaCompany);
        contact4.setEmail("uzeteny.takacs@takacs.hu");
        contact4.setComment("Takacs");
        contact4.setPhone("+14155552671");
        contact4.setStatus(Status.ACTIVE);
        contactRepository.save(contact4);

        Contact contact5 = new Contact();
        contact5.setFirstName("Passive");
        contact5.setLastName("Balogh");
        contact5.setCompany(gammaCompany);
        contact5.setEmail("passive.balogh@gamma.hu");
        contact5.setComment("zzz");
        contact5.setPhone("+14155552671");
        contact5.setStatus(Status.DELETED);
        contactRepository.save(contact5);

        Contact contact = new Contact();
        contact.setFirstName("Z");
        contact.setLastName("Z");
        contact.setCompany(gammaCompany);
        contact.setEmail("z.z@z.com");
        contact.setComment("zzz");
        contact.setPhone("+3699999999");
        contact.setStatus(Status.ACTIVE);

        for (int i = 0; i < 20; i++) {
            contactRepository.save((Contact)contact.clone());
        }

    }

    private static void initCompanies(CompanyRepository companyRepository) {
        Company company1 = new Company();
        company1.setName(TestUtils.ALPHA_COMPANY);
        Company company2 = new Company();
        company2.setName(TestUtils.BETA_COMPANY);
        Company company3 = new Company();
        company3.setName(TestUtils.GAMMA_COMPANY);
        companyRepository.save(company1);
        companyRepository.save(company2);
        companyRepository.save(company3);

    }
}
