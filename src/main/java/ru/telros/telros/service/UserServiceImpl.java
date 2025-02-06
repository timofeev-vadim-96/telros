package ru.telros.telros.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.telros.telros.exception.EmailAlreadyExistsException;
import ru.telros.telros.exception.EntityNotFoundException;
import ru.telros.telros.exception.UserNotFoundException;
import ru.telros.telros.model.User;
import ru.telros.telros.repository.UserDao;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    @Transactional
    public User create(User user) {
        if (userDao.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Пользователь с таким email уже существует");
        }

        return userDao.save(user);
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email = %s is not found".formatted(email)));
    }

    /**
     * Получение пользователя по идентификатору
     *
     * @return пользователь
     */
    @Override
    @Transactional(readOnly = true)
    public User getById(long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = %d is not found".formatted(id)));
    }

    /**
     * Получение текущего пользователя сессии
     *
     * @return текущий пользователь
     */
    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByEmail(email);
    }

    /**
     * Получение пользователя по login в рамках интерфейса UserDetailsService Spring Security
     * @param email принципала
     * @return пользователя
     * @throws UsernameNotFoundException если пользователь с таким email не найден
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userDao.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User with login: %s not found".formatted(email)));
    }
}
