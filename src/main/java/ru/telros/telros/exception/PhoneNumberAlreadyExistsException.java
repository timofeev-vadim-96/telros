package ru.telros.telros.exception;

import org.springframework.dao.DataAccessException;

public class PhoneNumberAlreadyExistsException extends DataAccessException {
    public PhoneNumberAlreadyExistsException(String message) {
        super(message);
    }
}
