package com.ksteindl.contacts.web.controller;

import com.ksteindl.contacts.domain.entities.AppUser;
import com.ksteindl.contacts.security.JwtAuthenticationFilter;
import com.ksteindl.contacts.security.JwtProvider;
import com.ksteindl.contacts.security.payload.JwtLoginResponse;
import com.ksteindl.contacts.security.payload.LoginRequest;
import com.ksteindl.contacts.web.WebUtils;
import com.ksteindl.contacts.web.input.AppUserInput;
import com.ksteindl.contacts.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public ResponseEntity<AppUser> registerUser(@Valid @RequestBody AppUserInput userInput, BindingResult result) {
        WebUtils.throwExceptionIfNotValid(result);
        AppUser newUser = appUserService.saveUser(userInput);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtLoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        WebUtils.throwExceptionIfNotValid(result);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = JwtAuthenticationFilter.TOKEN_PREFIX + jwtProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtLoginResponse(true, jwt));

    }

}


