package ru.telros.telros.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.controller.dto.UserInfoViewDto;
import ru.telros.telros.converter.PhoneNumberConverterImpl;
import ru.telros.telros.converter.UserConverterImpl;
import ru.telros.telros.exception.EntityNotFoundException;
import ru.telros.telros.exception.PhoneNumberAlreadyExistsException;
import ru.telros.telros.model.User;
import ru.telros.telros.repository.SearchCriteriaWithPaginationUserDao;
import ru.telros.telros.repository.SearchCriteriaWithPaginationUserDaoImpl;
import ru.telros.telros.repository.UserDao;
import ru.telros.telros.service.dto.UserInfoDto;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Сервис для работы с детальной информацией о пользователе")
@DataJpaTest
@Import({UserInfoServiceImpl.class, SearchCriteriaWithPaginationUserDaoImpl.class, UserConverterImpl.class,
        PhoneNumberConverterImpl.class})
@Transactional(propagation = Propagation.NEVER)
class UserInfoServiceImplTest {
    @Autowired
    private UserInfoService infoService;

    @SpyBean
    private UserDao userDao;

    @SpyBean
    private SearchCriteriaWithPaginationUserDao criteriaDao;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void get(long id) {
        UserInfoDto infoDto = infoService.get(id);

        assertThat(infoDto).isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", id);
        verify(userDao, times(1)).findById(id);
    }

    @ParameterizedTest
    @MethodSource("getSearchArgs")
    void getAll(String firstName, String secondName, String patronymic, LocalDate birthDay, String phoneNumber,
                int expectedResultSize) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        Page<UserInfoDto> users = infoService.getAll(firstName, secondName, patronymic, birthDay, phoneNumber,
                pageable);

        assertEquals(expectedResultSize, users.getTotalElements());
        assertEquals(1, users.getTotalPages());
        verify(criteriaDao, times(1)).findAll(any(), any());
    }

    @Test
    void updateThrowsWhenUserDoesNotExists() {
        long notExistingUserId = 11L;
        UserInfoViewDto dto = UserInfoViewDto.builder().id(notExistingUserId).build();

        assertThrowsExactly(EntityNotFoundException.class, () -> infoService.update(dto));
    }

    @Test
    void update() {
        UserInfoViewDto dto = UserInfoViewDto.builder()
                .id(1L)
                .firstName("updated first name")
                .secondName("updated second name")
                .patronymic("updated patronymic")
                .birthDay(LocalDate.now())
                .build();

        UserInfoDto updated = infoService.update(dto);

        assertThat(updated).isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", dto.getId())
                .hasFieldOrPropertyWithValue("firstName", dto.getFirstName())
                .hasFieldOrPropertyWithValue("secondName", dto.getSecondName())
                .hasFieldOrPropertyWithValue("patronymic", dto.getPatronymic())
                .hasFieldOrPropertyWithValue("birthDay", dto.getBirthDay());
        verify(userDao, times(1)).findById(dto.getId());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void addPhoneNumber() {
        PhoneNumberDto phoneNumberDto = PhoneNumberDto.builder()
                .phoneNumber("82223334455")
                .userId(1L)
                .build();

        UserInfoDto dto = infoService.addPhoneNumber(phoneNumberDto);

        assertThat(dto).isNotNull()
                .hasNoNullFieldsOrProperties();
        assertThat(dto.getPhoneNumbers()).isNotEmpty()
                .filteredOn(phone -> phone.getPhoneNumber().equals(phoneNumberDto.getPhoneNumber()))
                .isNotEmpty();
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void addPhoneNumberThrowsWhenUserDoesNotExists() {
        long notExistingUserId = 11L;
        PhoneNumberDto phoneNumberDto = PhoneNumberDto.builder()
                .userId(notExistingUserId)
                .build();

        assertThrowsExactly(EntityNotFoundException.class, () -> infoService.addPhoneNumber(phoneNumberDto));
    }

    @Test
    void addPhoneNumberThrowsWhenNumberIsAlreadyExists() {
        PhoneNumberDto phoneNumberDto = PhoneNumberDto.builder()
                .userId(1L)
                .phoneNumber("+7 (121) 111-11-11")
                .build();

        assertThrowsExactly(PhoneNumberAlreadyExistsException.class, () -> infoService.addPhoneNumber(phoneNumberDto));
    }

    @Test
    void deletePhoneNumber() {
        long userId = 1L;
        long phoneNumberId = 1L;

        UserInfoDto user = infoService.deletePhoneNumber(userId, phoneNumberId);

        assertThat(user).isNotNull().hasNoNullFieldsOrProperties();
        assertThat(user.getPhoneNumbers()).isNotEmpty()
                .filteredOn(p -> p.getId() == phoneNumberId)
                .isEmpty();
    }

    @Test
    void deletePhoneNumberThrowsWhenUserDoesNotExists() {
        long notExistingUserId = 11L;
        long phoneNumberId = 11L;

        assertThrowsExactly(EntityNotFoundException.class, ()
                -> infoService.deletePhoneNumber(notExistingUserId, phoneNumberId));
    }

    @Test
    void deleteById() {
        long userId = 1L;
        Optional<User> byId = userDao.findById(userId);
        assertTrue(byId.isPresent());

        infoService.deleteById(userId);
        Optional<User> afterDelete = userDao.findById(userId);

        assertTrue(afterDelete.isEmpty());
    }

    private static Stream<Arguments> getSearchArgs() {
        return Stream.of(
                Arguments.of(null, null, null, null, null, 10),
                Arguments.of("Admin", null, null, null, null, 1),
                Arguments.of("User", null, null, null, null, 9),
                Arguments.of(null, "Second", null, null, null, 1),
                Arguments.of(null, null, "Adminovich", null, null, 1),
                Arguments.of(null, null, "Userovich", null, null, 9),
                Arguments.of(null, null, null, LocalDate.of(1980, 1, 15), null, 1),
                Arguments.of(null, null, null, null, "+7 (121) 111-11-11", 1),
                Arguments.of(null, null, null, null, "81199999999", 1)
        );
    }
}