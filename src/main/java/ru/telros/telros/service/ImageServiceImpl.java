package ru.telros.telros.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.telros.telros.exception.EmptyFileException;
import ru.telros.telros.exception.ImageNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    private static final String BUCKET_NAME = "telros-bucket";

    private final MinioClient minioClient;

    /**
     * Сохранение изображения пользователя с именем, эквивалентным его идентификатору
     *
     * @param file   изображение
     * @param userId идентификатор пользователя
     * @return сообщение о результате сохранения изображения
     */
    @Override
    public String uploadImage(long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException("File is empty");
        }
        createBucketIfNotExists(BUCKET_NAME);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(BUCKET_NAME)
                    .object(String.valueOf(userId))
                    .stream(inputStream, inputStream.available(), -1)
                    .build());

            return "The image has been uploaded successfully";
        } catch (Exception e) {
            return "The file could not be uploaded";
        }
    }

    @Override
    public byte[] downloadImage(long userId) {
        try (InputStream stream =
                     minioClient.getObject(GetObjectArgs
                             .builder()
                             .bucket(BUCKET_NAME)
                             .object(String.valueOf(userId))
                             .build())) {
            return IOUtils.toByteArray(stream);
        } catch (Exception e) {
            throw new ImageNotFoundException("Image with name = %s is not found".formatted(String.valueOf(userId)));
        }
    }

    @Override
    public void removeImage(long userId) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(String.valueOf(userId))
                            .build());
        } catch (Exception e) {
            logger.error("Exception when trying to delete an image: {}", e.getMessage());
        }
    }

    private void removeBucket(String bucketName) {
        RemoveBucketArgs build = RemoveBucketArgs.builder()
                .bucket(bucketName)
                .build();
    }

    private void createBucketIfNotExists(String name) {
        try {
            boolean isAlreadyExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(name)
                            .build());
            if (!isAlreadyExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(name)
                        .build());
            }
        } catch (Exception e) {
            logger.error("An exception occurred when trying to create a new one bucket with name = {}", name);
        }
    }
}
