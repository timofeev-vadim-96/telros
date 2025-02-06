package ru.telros.telros.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.telros.telros.controller.dto.PhoneNumberDto;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Детальная информация о пользователе")
public class UserInfoDto {
    @Schema(description = "Уникальный идентификатор")
    @NotNull(message = "id пользователя не должно быть null")
    private Long id;

    @Schema(description = "Имя", example = "Иван")
    @Size(min = 3, max = 50, message = "Имя должно содержать от 3 до 50 символов")
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    @Size(min = 3, max = 50, message = "Фамилия должна содержать от 3 до 50 символов")
    private String secondName;

    @Schema(description = "Отчество", example = "Иванович")
    @Size(min = 3, max = 50, message = "Отчество должно содержать от 3 до 50 символов")
    private String patronymic;

    @Schema(description = "Дата рождения", example = "yyyy-MM-dd")
    private LocalDate birthDay;

    @Schema(description = "Адрес электронной почты", example = "jondoe@gmail.com")
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    @Schema(description = "Номера мобильных телефонов")
    private Set<PhoneNumberDto> phoneNumbers;
}
