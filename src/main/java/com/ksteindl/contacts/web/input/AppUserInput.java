package com.ksteindl.contacts.web.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AppUserInput {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

}
