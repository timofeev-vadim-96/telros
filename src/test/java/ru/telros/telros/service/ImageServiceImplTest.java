package ru.telros.telros.service;

import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MinIOContainer;
import ru.telros.telros.exception.EmptyFileException;
import ru.telros.telros.exception.ImageNotFoundException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с картинками")
class ImageServiceImplTest {
    private static final MinIOContainer minioContainer = new MinIOContainer("minio/minio:latest");

    private static final byte[] BYTE_FILE_REPRESENTATION = "FILE".getBytes();

    private final static MockMultipartFile MULTIPART_FILE =
            new MockMultipartFile("tempFileName", BYTE_FILE_REPRESENTATION);

    private static final long EXISTING_USER_ID = 1L;

    private static MinioClient minioClient;

    private static ImageService imageService;


    @BeforeAll
    static void setUp() {
        minioContainer.start();
        minioClient = MinioClient
                .builder()
                .endpoint(minioContainer.getS3URL())
                .credentials(minioContainer.getUserName(), minioContainer.getPassword())
                .build();
        imageService = new ImageServiceImpl(minioClient);
    }

    @Test
    void uploadImage() {
        String resultString = imageService.uploadImage(EXISTING_USER_ID, MULTIPART_FILE);

        assertTrue(resultString.contains("successfully"));
    }

    @Test
    void uploadImageThrowsWhenFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        assertThrows(EmptyFileException.class, () -> {
            imageService.uploadImage(1L, emptyFile);
        });
    }

    @Test
    void downloadImage() {
        imageService.uploadImage(EXISTING_USER_ID, MULTIPART_FILE);

        byte[] bytes = imageService.downloadImage(EXISTING_USER_ID);

        assertArrayEquals(BYTE_FILE_REPRESENTATION, bytes);
    }

    @Test
    void downloadImageThrowsWhenDoesNotExists() {
        long notExistingUserId = 11L;

        assertThrowsExactly(ImageNotFoundException.class, () -> imageService.downloadImage(notExistingUserId));
    }

    @Test
    void removeImage() {
        imageService.uploadImage(EXISTING_USER_ID, MULTIPART_FILE);
        assertDoesNotThrow(() -> imageService.downloadImage(EXISTING_USER_ID));

        imageService.removeImage(EXISTING_USER_ID);

        assertThrowsExactly(ImageNotFoundException.class, () -> imageService.downloadImage(EXISTING_USER_ID));
    }
}