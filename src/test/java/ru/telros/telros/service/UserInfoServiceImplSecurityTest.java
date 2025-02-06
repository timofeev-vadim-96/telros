package ru.telros.telros.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.controller.dto.UserInfoViewDto;
import ru.telros.telros.exception.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@DisplayName("Тест безопасности сервиса для работы с детальной информацией о пользователе")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserInfoServiceImplSecurityTest {
    private final static String ADMIN_EMAIL = "testAdmin@gmail.com"; //id = 1

    private final static String USER_EMAIL = "user2@example.com"; //id = 2

    @Autowired
    private UserInfoService infoService;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @WithMockUser(username = ADMIN_EMAIL)
    void getByAdmin(long id) {
        assertDoesNotThrow(() -> infoService.get(id));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void getByUser() {
        long userId = 2;
        assertDoesNotThrow(() -> infoService.get(userId));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void getByUserDeniedOnAnotherUserId() {
        long anotherUserId = 3;
        assertThrowsExactly(AccessDeniedException.class, () -> infoService.get(anotherUserId));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL)
    void getAllByAdmin() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));

        assertDoesNotThrow(() -> infoService
                .getAll(null, null, null, null, null, pageable));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL)
    void updateByAdmin() {
        long userId = 1L;
        UserInfoViewDto dto = UserInfoViewDto.builder().id(userId).build();

        assertDoesNotThrow(() -> infoService.update(dto));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void updateByUserDeniedOnAnotherUserId() {
        long userId = 1L;
        UserInfoViewDto dto = UserInfoViewDto.builder().id(userId).build();

        assertThrowsExactly(AccessDeniedException.class, () -> infoService.update(dto));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL)
    void addPhoneNumberByAdmin() {
        PhoneNumberDto phoneNumberDto = PhoneNumberDto.builder()
                .phoneNumber("82223334455")
                .userId(1L)
                .build();

        assertDoesNotThrow(() -> infoService.addPhoneNumber(phoneNumberDto));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void addPhoneNumberByUserDeniedOnAnotherUserId() {
        PhoneNumberDto phoneNumberDto = PhoneNumberDto.builder()
                .phoneNumber("82223334455")
                .userId(1L)
                .build();

        assertThrowsExactly(AccessDeniedException.class, () -> infoService.addPhoneNumber(phoneNumberDto));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL)
    void deletePhoneNumberByAdmin() {
        long userId = 1L;
        long phoneNumberId = 1L;

        assertDoesNotThrow(() -> infoService.deletePhoneNumber(userId, phoneNumberId));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void deletePhoneNumberByUserDeniedOnAnotherUserId() {
        long userId = 1L;
        long phoneNumberId = 1L;

        assertThrowsExactly(AccessDeniedException.class, () -> infoService.deletePhoneNumber(userId, phoneNumberId));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteByAdmin() {
        long userId = 3L;

        assertDoesNotThrow(() -> infoService.deleteById(userId));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void deleteByUserDeniedOnAnotherUserId() {
        long userId = 1L;

        assertThrowsExactly(AccessDeniedException.class, () -> infoService.deleteById(userId));
    }
}