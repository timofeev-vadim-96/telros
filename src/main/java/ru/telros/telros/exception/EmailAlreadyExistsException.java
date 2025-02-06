package ru.telros.telros.exception;

import org.springframework.dao.DataAccessException;

public class EmailAlreadyExistsException extends DataAccessException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
