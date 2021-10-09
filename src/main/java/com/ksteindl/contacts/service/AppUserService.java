package com.ksteindl.contacts.service;


import com.ksteindl.contacts.domain.entities.AppUser;
import com.ksteindl.contacts.exception.ValidationException;
import com.ksteindl.contacts.web.input.AppUserInput;
import com.ksteindl.contacts.domain.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AppUser saveUser(AppUserInput userInput) {
        AppUser appUser = new AppUser();
        appUser.setUsername(userInput.getUsername());
        String encodedPassword = bCryptPasswordEncoder.encode(userInput.getPassword());
        appUser.setPassword(encodedPassword);
        try {
            return appUserRepository.save(appUser);
        } catch (DataIntegrityViolationException exception) {
            throw new ValidationException("Username is already takne");
        }

    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);

    }

}
