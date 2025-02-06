package ru.telros.telros.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.telros.telros.exception.EmailAlreadyExistsException;
import ru.telros.telros.exception.EntityNotFoundException;
import ru.telros.telros.exception.UserNotFoundException;
import ru.telros.telros.model.User;
import ru.telros.telros.repository.UserDao;
import ru.telros.telros.util.Role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Сервис для работы с пользователями")
@DataJpaTest
@Import({UserServiceImpl.class})
@Transactional(propagation = Propagation.NEVER)
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;

    @SpyBean
    private UserDao userDao;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void create() {
        User newUser = User.builder()
                .email("newUserEmail@gmail.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        User user = userService.create(newUser);

        assertThat(user).isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(newUser);
        verify(userDao, times(1)).existsByEmail(newUser.getEmail());
        verify(userDao, times(1)).save(newUser);
    }

    @Test
    void createThrowsWhenEmailExists() {
        String alreadyExistsEmail = "user2@example.com";
        User newUser = User.builder()
                .email(alreadyExistsEmail)
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        assertThrowsExactly(EmailAlreadyExistsException.class, () -> userService.create(newUser));
        verify(userDao, times(1)).existsByEmail(alreadyExistsEmail);
    }

    @Test
    void getUserByEmail() {
        String expectedEmail = "user2@example.com";

        User user = userService.getUserByEmail(expectedEmail);

        assertThat(user).isNotNull().hasFieldOrPropertyWithValue("email", expectedEmail);
        verify(userDao, times(1)).findByEmail(expectedEmail);
    }

    @Test
    void getUserByEmailNegative() {
        String notExistingEmail = "notExist@gmail.com";

        assertThrowsExactly(EntityNotFoundException.class, () -> userService.getUserByEmail(notExistingEmail));
        verify(userDao, times(1)).findByEmail(notExistingEmail);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getById(long id) {
        User user = userService.getById(id);

        assertThat(user).isNotNull().hasFieldOrPropertyWithValue("id", id);
        verify(userDao, times(1)).findById(id);
    }

    @Test
    void getByIdNegative() {
        long notExistingId = 11L;

        assertThrowsExactly(EntityNotFoundException.class, () -> userService.getById(notExistingId));
        verify(userDao, times(1)).findById(notExistingId);
    }

    @Test
    @WithUserDetails(value = "user2@example.com")
    void getCurrentUser() {
        String expectedEmail = "user2@example.com";

        User user = userService.getCurrentUser();

        assertEquals(expectedEmail, user.getEmail());
    }

    @Test
    void loadUserByUsername() {
        String expectedUsername = "user2@example.com";

        UserDetails userDetails = userService.loadUserByUsername(expectedUsername);

        assertNotNull(userDetails);
        assertEquals(expectedUsername, userDetails.getUsername());
        verify(userDao, times(1)).findByEmail(expectedUsername);
    }

    @Test
    void loadUserByUsernameNegative() {
        String notExistingEmail = "notExist@gmail.com";

        assertThrowsExactly(UserNotFoundException.class, () -> userService.loadUserByUsername(notExistingEmail));
        verify(userDao, times(1)).findByEmail(notExistingEmail);
    }
}