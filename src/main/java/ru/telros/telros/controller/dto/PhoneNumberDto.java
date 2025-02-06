package ru.telros.telros.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Номер телефона пользователя")
public class PhoneNumberDto {
    @Schema(description = "Уникальный идентификатор номера телефона")
    @Nullable
    private Long id;

    @Schema(description = "Номер телефона", example = "+7 (111) 111-11-11, +7 (111) 111 11 1 или 81111111111")
    @Pattern(regexp = "^(?:\\+7\\s*\\(\\d{3}\\)\\s*[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}|\\+7\\s*\\d{10}|8\\d{10})$",
            message = "Неверный формат мобильного номера")
    private String phoneNumber;

    @Schema(description = "Уникальный идентификатор пользователя")
    @NotNull(message = "идентификатор пользователя не должен быть null")
    private Long userId;
}
