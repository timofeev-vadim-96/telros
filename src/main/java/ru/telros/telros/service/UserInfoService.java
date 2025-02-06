package ru.telros.telros.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.controller.dto.UserInfoViewDto;
import ru.telros.telros.service.dto.UserInfoDto;

import java.time.LocalDate;

public interface UserInfoService {
    UserInfoDto get(long id);

    Page<UserInfoDto> getAll(String firstName, String secondName, String patronymic,
                             LocalDate birthDay, String phoneNumber, Pageable pageable);

    UserInfoDto update(UserInfoViewDto dto);

    UserInfoDto addPhoneNumber(PhoneNumberDto dto);

    UserInfoDto deletePhoneNumber(long userId, long phoneNumberId);

    void deleteById(long id);
}
