package com.ksteindl.contacts.web.input;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ContactInput {

    @NotBlank(message = "First name cannot be blank (firstName)")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank (lastName)")
    private String lastName;

    @Email(message = "Wrong e-mail format! Please try something like this: 'john.doe@company.com'")
    @NotBlank(message = "e-mail cannot be blank (email)")
    private String email;

    private String phone;

    @NotNull(message = "Company is required (companyId)")
    private Long companyId;

    private String comment;

    @NotBlank(message = "Status cannot be blank (status)")
    private String status;



}
