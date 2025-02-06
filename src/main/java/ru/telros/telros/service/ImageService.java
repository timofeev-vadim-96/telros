package ru.telros.telros.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(long userId, MultipartFile image);

    byte[] downloadImage(long userId);

    void removeImage(long userId);
}
