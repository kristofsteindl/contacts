package com.ksteindl.contacts.web.controller;

import com.ksteindl.contacts.domain.entities.AppUser;
import com.ksteindl.contacts.web.WebUtils;
import com.ksteindl.contacts.web.input.AppUserInput;
import com.ksteindl.contacts.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/users")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;


    @PostMapping("/register")
    public ResponseEntity<AppUser> registerUser(@Valid @RequestBody AppUserInput userInput, BindingResult result) {
        WebUtils.throwExceptionIfNotValid(result);
        AppUser newUser = appUserService.saveUser(userInput);
        return ResponseEntity.ok(newUser);
    }

}


