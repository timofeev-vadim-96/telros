package ru.telros.telros.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * Catches exceptions when email is already exists
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Void> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    /**
     * Catches exceptions when phone number is already exists
     */
    @ExceptionHandler(PhoneNumberAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Void> handlePhoneNumberAlreadyExistsException(PhoneNumberAlreadyExistsException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TokenNotValidException.class)
    public ResponseEntity<Void> handleTokenNotValidException(TokenNotValidException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<Void> handleEmptyFileException(EmptyFileException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Catches errors when the desired or nested entity is not found.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Catches errors when the image is not found.
     */
    @ExceptionHandler(ImageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Void> handleImageNotFoundException(ImageNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Catch exceptions when trying to authenticate with a non-existent email
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Void> handleUserNotFoundException(UserNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Catches exceptions when user attempts to access the resource of other users.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
