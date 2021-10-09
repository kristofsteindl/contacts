package com.ksteindl.contacts;


import com.ksteindl.contacts.domain.entities.Company;
import com.ksteindl.contacts.domain.repository.CompanyRepository;
import com.ksteindl.contacts.security.JwtProvider;
import com.ksteindl.contacts.service.AppUserService;
import com.ksteindl.contacts.service.ContactService;
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
            @Autowired ContactService contactService,
            @Autowired JwtProvider jwtProvider) {
        if (first) {
            appUserService.saveUser(TestUtils.getAdminInput());
            initCompanies(companyRepository);


            TOKEN_FOR_ADMIN = jwtProvider.generateToken(TestUtils.ADMIN_USERNAME);
            first = false;
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
