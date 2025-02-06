package ru.telros.telros.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Детальная информация о пользователе")
public class UserInfoViewDto {
    @Schema(description = "Уникальный идентификатор")
    @NotNull(message = "id пользователя не должно быть null")
    private Long id;

    @Schema(description = "Имя", example = "Иван")
    @Size(min = 3, max = 50, message = "Имя должно содержать от 3 до 50 символов")
    @Nullable
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    @Size(min = 3, max = 50, message = "Фамилия должна содержать от 3 до 50 символов")
    @Nullable
    private String secondName;

    @Schema(description = "Отчество", example = "Иванович")
    @Size(min = 3, max = 50, message = "Отчество должно содержать от 3 до 50 символов")
    @Nullable
    private String patronymic;

    @Schema(description = "Дата рождения", example = "yyyy-MM-dd")
    @Nullable
    private LocalDate birthDay;
}
