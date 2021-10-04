package com.ksteindl.contacts.web;

import com.ksteindl.contacts.domain.entities.Company;
import com.ksteindl.contacts.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/company")
@CrossOrigin
public class CompanyController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public ResponseEntity<Iterable<Company>> getCompanies() {
        Iterable<Company> companies = contactService.getCompanies();
        return ResponseEntity.ok().body(companies);
    }

}
