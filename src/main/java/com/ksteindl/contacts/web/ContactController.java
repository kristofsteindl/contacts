package com.ksteindl.contacts.web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/contact")
public class ContactController {

    @GetMapping
    public ResponseEntity<String> getWelcome() {
        return ResponseEntity.status(200).body("hello world");
    }


}
