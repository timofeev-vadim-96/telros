package ru.telros.telros.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.telros.telros.model.User;

public interface UserService extends UserDetailsService {
    User create(User user);

    User getUserByEmail(String email);

    User getById(long id);

    User getCurrentUser();
}
