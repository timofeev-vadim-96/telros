package ru.telros.telros.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.model.PhoneNumber;
import ru.telros.telros.model.User;
import ru.telros.telros.service.dto.UserInfoDto;
import ru.telros.telros.util.Role;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Конвертер пользователей")
@SpringBootTest(classes = {UserConverterImpl.class})
class UserConverterImplTest {
    @Autowired
    private UserConverterImpl userConverter;

    @MockBean
    private PhoneNumberConverter phoneNumberConverter;

    private PhoneNumber numbEntity;

    private User userEntity;

    private PhoneNumberDto numberDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        numbEntity = new PhoneNumber(1L, "+7 (111) 943-93-48", null);
        userEntity = User.builder()
                .id(1L)
                .firstName("name")
                .secondName("secondName")
                .patronymic("patronymic")
                .birthDay(LocalDate.now())
                .email("someEmail@ya.ru")
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .phoneNumbers(Set.of(numbEntity))
                .build();
        numbEntity.setUser(userEntity);
        numberDto = PhoneNumberDto.builder()
                .id(numbEntity.getId())
                .phoneNumber(numbEntity.getPhoneNumber())
                .userId(numbEntity.getUser().getId())
                .build();
        when(phoneNumberConverter.convertToDto(any(PhoneNumber.class))).thenReturn(numberDto);
        when(phoneNumberConverter.convertToDtos(any(Set.class))).thenReturn(Set.of(numberDto));
    }

    @Test
    void convertToDto() {
        UserInfoDto dto = userConverter.convertToDto(userEntity);

        compareResultDtoWithEntity(dto);
    }

    @Test
    void convertToDtos() {
        List<UserInfoDto> users = userConverter.convertToDtos(List.of(userEntity));

        assertThat(users).isNotNull().isNotEmpty().hasSize(1);
        UserInfoDto dto = users.get(0);

        compareResultDtoWithEntity(dto);
    }

    private void compareResultDtoWithEntity(UserInfoDto dto) {
        assertThat(dto).isNotNull()
                .hasFieldOrPropertyWithValue("id", userEntity.getId())
                .hasFieldOrPropertyWithValue("firstName", userEntity.getFirstName())
                .hasFieldOrPropertyWithValue("secondName", userEntity.getSecondName())
                .hasFieldOrPropertyWithValue("patronymic", userEntity.getPatronymic())
                .hasFieldOrPropertyWithValue("birthDay", userEntity.getBirthDay())
                .hasFieldOrPropertyWithValue("email", userEntity.getEmail());
        assertThat(dto.getPhoneNumbers())
                .isNotNull()
                .isNotEmpty();
        PhoneNumberDto phoneDto = dto.getPhoneNumbers().stream().findFirst().get();
        assertThat(phoneDto).isNotNull()
                .hasFieldOrPropertyWithValue("id", numbEntity.getId())
                .hasFieldOrPropertyWithValue("phoneNumber", numbEntity.getPhoneNumber())
                .hasFieldOrPropertyWithValue("userId", numbEntity.getUser().getId());
    }
}