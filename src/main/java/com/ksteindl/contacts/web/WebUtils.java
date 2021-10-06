package com.ksteindl.contacts.web;

import com.ksteindl.contacts.exception.ValidationException;
import com.ksteindl.contacts.web.controller.ContactController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

public class WebUtils {

    private static Logger logger = LoggerFactory.getLogger(WebUtils.class);

    public static void throwExceptionIfNotValid(BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errorMap = result.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (errorMessage1, errorMessage2) -> errorMessage1 + ", " + errorMessage2));
            logger.info("ValidationException is being thrown with errorMap: {0}", errorMap);
            throw new ValidationException(errorMap);
        }
    }

}
