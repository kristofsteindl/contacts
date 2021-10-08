package com.ksteindl.contacts.service;


import com.ksteindl.contacts.domain.entities.AppUser;
import com.ksteindl.contacts.web.input.AppUserInput;
import com.ksteindl.contacts.domain.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
        return appUserRepository.save(appUser);
    }

}
