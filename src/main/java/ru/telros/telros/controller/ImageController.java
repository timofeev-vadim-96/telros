package ru.telros.telros.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.telros.telros.service.ImageService;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Контроллер изображений", description = "Контроллер для работы с изображениями пользователей")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/api/v1/image")
    @Operation(summary = "Загрузить изображение")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "изображение успешно загружено"),
            @ApiResponse(responseCode = "400", description = "изображение отсутствует")
    })
    public ResponseEntity<String> uploadImage(
            @RequestBody MultipartFile file,
            @RequestParam("userId") long userId) {
        String resultMessage = imageService.uploadImage(userId, file);
        return new ResponseEntity<>(resultMessage, HttpStatus.OK);
    }

    @GetMapping(value = "/api/v1/image/{userId}",
            produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "Скачать изображение")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "изображение успешно скачано"),
            @ApiResponse(responseCode = "404", description = "изображение не найдено")
    })
    public ResponseEntity<byte[]> getImage(@PathVariable("userId") long userId) {
        byte[] bytes = imageService.downloadImage(userId);
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @DeleteMapping("/api/v1/image/{userId}")
    @Operation(summary = "Удалить изображение")
    @ApiResponse(responseCode = "200", description = "изображение удалено")
    public ResponseEntity<Void> removeImage(@PathVariable("userId") long userId) {
        imageService.removeImage(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
