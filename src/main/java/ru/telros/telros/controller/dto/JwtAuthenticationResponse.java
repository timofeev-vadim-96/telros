package ru.telros.telros.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Ответ на успешную аутентификацию, содержащий токены")
public class JwtAuthenticationResponse {
    private String accessToken;

    private String refreshToken;
}
