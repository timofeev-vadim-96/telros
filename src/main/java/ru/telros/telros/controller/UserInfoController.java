package ru.telros.telros.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.controller.dto.UserInfoViewDto;
import ru.telros.telros.service.UserInfoService;
import ru.telros.telros.service.dto.UserInfoDto;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Контроллер пользователей", description = "Контроллер для работы с детальной информацией о пользователях")
public class UserInfoController {
    private final UserInfoService userInfoService;

    @GetMapping("api/v1/user/{id}")
    @Operation(summary = "Получение пользователя по идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "задание найдено"),
            @ApiResponse(responseCode = "403", description = "попытка получить доступ к инфо о другом пользователе"),
            @ApiResponse(responseCode = "404", description = "id задания не корректно")
    })
    public ResponseEntity<UserInfoDto> get(@PathVariable("id") long id) {
        UserInfoDto user = userInfoService.get(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("api/v1/user")
    @Operation(summary = "Получение пользователей с пагинацией и фильтрацией по заданным параметрам")
    @ApiResponse(responseCode = "200", description = "пользователи найдены")
    public ResponseEntity<Page<UserInfoDto>> getAll(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "secondName", required = false) String secondName,
            @RequestParam(value = "patronymic", required = false) String patronymic,
            @RequestParam(value = "birthDay", required = false) LocalDate birthDay,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @PageableDefault(
                    size = 20,
                    page = 0,
                    sort = {"id"},
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<UserInfoDto> tasks =
                userInfoService.getAll(firstName, secondName, patronymic, birthDay, phoneNumber, pageable);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PatchMapping("api/v1/user")
    @Operation(summary = "Обновление информации о пользователе")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "информация о пользователе обновлена"),
            @ApiResponse(responseCode = "403", description = "попытка обновить инфо о другом пользователе"),
            @ApiResponse(responseCode = "404", description = "пользователь с таким id не найден")
    })
    public ResponseEntity<UserInfoDto> update(@Valid @RequestBody UserInfoViewDto dto) {
        UserInfoDto updated = userInfoService.update(dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("api/v1/user/{id}")
    @Operation(summary = "Удаление пользователя")
    @ApiResponse(responseCode = "200", description = "пользователь удален")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        userInfoService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("api/v1/user/{userId}/phone/{phoneNumberId}")
    @Operation(summary = "Удаление номера телефона пользователя")
    @ApiResponse(responseCode = "200", description = "пользователь удален")
    public ResponseEntity<UserInfoDto> deletePhoneNumber(@PathVariable("userId") long userId,
                                                         @PathVariable("phoneNumberId") long phoneNumberId) {
        UserInfoDto user = userInfoService.deletePhoneNumber(userId, phoneNumberId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("api/v1/user/phone")
    @Operation(summary = "Добавление номера телефона пользователю")
    public ResponseEntity<UserInfoDto> addPhoneNumber(@Valid @RequestBody PhoneNumberDto dto) {
        UserInfoDto user = userInfoService.addPhoneNumber(dto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
