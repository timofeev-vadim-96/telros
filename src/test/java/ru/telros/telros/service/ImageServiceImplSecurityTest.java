package ru.telros.telros.service;

import io.minio.MinioClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import ru.telros.telros.exception.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@DisplayName("Тест безопаности сервиса для работы с картинками")
@SpringBootTest
class ImageServiceImplSecurityTest {
    private final static String ADMIN_EMAIL = "testAdmin@gmail.com"; //id = 1

    private final static String USER_EMAIL = "user2@example.com"; //id = 2

    private static final byte[] BYTE_FILE_REPRESENTATION = "FILE".getBytes();

    private static final MockMultipartFile MULTIPART_FILE =
            new MockMultipartFile("tempFileName", BYTE_FILE_REPRESENTATION);

    private static final long USER_ID = 2L;

    private static final long ANOTHER_USER_ID = 3L;

    @MockBean
    private static MinioClient minioClient;

    @Autowired
    private ImageService imageService;

    @Test
    @WithMockUser(username = ADMIN_EMAIL)
    void uploadImageByAdmin() {
        assertDoesNotThrow(() -> imageService.uploadImage(ANOTHER_USER_ID, MULTIPART_FILE));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void uploadImageByUserSuccessfully() {
        assertDoesNotThrow(() -> imageService.uploadImage(USER_ID, MULTIPART_FILE));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void uploadImageByUserDeniedOnAnotherUserId() {
        assertThrowsExactly(AccessDeniedException.class,
                () -> imageService.uploadImage(ANOTHER_USER_ID, MULTIPART_FILE));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL)
    void downloadImageByAdmin() {
        assertDoesNotThrow(() -> imageService.uploadImage(ANOTHER_USER_ID, MULTIPART_FILE));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void downloadImageByUserDeniedOnAnotherUserId() {
        assertThrowsExactly(AccessDeniedException.class,
                () -> imageService.uploadImage(ANOTHER_USER_ID, MULTIPART_FILE));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void downloadImageByUserSuccessfully() {
        assertDoesNotThrow(() -> imageService.uploadImage(USER_ID, MULTIPART_FILE));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL)
    void removeImageByAdmin() {
        assertDoesNotThrow(() -> imageService.removeImage(ANOTHER_USER_ID));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void removeImageByUserOnAnotherUserId() {
        assertThrowsExactly(AccessDeniedException.class,
                () -> imageService.removeImage(ANOTHER_USER_ID));
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    void removeImageByUserSuccessfully() {
        assertDoesNotThrow(() -> imageService.removeImage(USER_ID));
    }
}