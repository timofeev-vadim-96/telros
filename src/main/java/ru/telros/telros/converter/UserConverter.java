package ru.telros.telros.converter;

import ru.telros.telros.model.User;
import ru.telros.telros.service.dto.UserInfoDto;

import java.util.List;

public interface UserConverter {
    UserInfoDto convertToDto(User entity);

    List<UserInfoDto> convertToDtos(List<User> entities);
}
