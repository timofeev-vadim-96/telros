package ru.telros.telros.converter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.model.PhoneNumber;
import ru.telros.telros.model.User;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Конвертер номеров телефонов")
class PhoneNumberConverterImplTest {
    private static PhoneNumberConverter converter;

    private PhoneNumber entity;

    private User user;

    @BeforeAll
    static void setUp() {
        converter = new PhoneNumberConverterImpl();
    }

    @BeforeEach
    void init() {
        user = User.builder().id(1L).build();
        entity = new PhoneNumber(1L, "+7 (111) 943-93-48", user);
    }


    @Test
    void convertToDto() {
        PhoneNumberDto dto = converter.convertToDto(entity);

        compareResultDtoWithEntity(dto);
    }

    @Test
    void convertToDtos() {
        Set<PhoneNumberDto> dtos = converter.convertToDtos(Set.of(entity));

        assertThat(dtos).isNotNull().isNotEmpty().hasSize(1);
        PhoneNumberDto dto = dtos.stream().findFirst().get();
        compareResultDtoWithEntity(dto);
    }

    private void compareResultDtoWithEntity(PhoneNumberDto dto) {
        assertThat(dto).isNotNull()
                .hasFieldOrPropertyWithValue("id", entity.getId())
                .hasFieldOrPropertyWithValue("phoneNumber", entity.getPhoneNumber())
                .hasFieldOrPropertyWithValue("userId", entity.getUser().getId());
    }
}