package ru.telros.telros.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.telros.telros.model.User;
import ru.telros.telros.service.dto.UserInfoDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserConverterImpl implements UserConverter {
    private final PhoneNumberConverter phoneMapper;

    @Override
    public UserInfoDto convertToDto(User entity) {
        UserInfoDto dto = UserInfoDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .build();
        if (entity.getFirstName() != null) {
            dto.setFirstName(entity.getFirstName());
        }
        if (entity.getSecondName() != null) {
            dto.setSecondName(entity.getSecondName());
        }
        if (entity.getPatronymic() != null) {
            dto.setPatronymic(entity.getPatronymic());
        }
        if (entity.getBirthDay() != null) {
            dto.setBirthDay(entity.getBirthDay());
        }
        if (entity.getPhoneNumbers() != null && !entity.getPhoneNumbers().isEmpty()) {
            dto.setPhoneNumbers(phoneMapper.convertToDtos(entity.getPhoneNumbers()));
        }
        return dto;
    }

    @Override
    public List<UserInfoDto> convertToDtos(List<User> entities) {
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
