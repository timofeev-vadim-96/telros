package ru.telros.telros.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.telros.telros.security.filter.JwtAuthenticationFilter;
import ru.telros.telros.service.ImageService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ImageController.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@DisplayName("Контроллер для работы с изображениями")
class ImageControllerTest {
    private static final byte[] BYTE_FILE_REPRESENTATION = "FILE".getBytes();

    private final static MockMultipartFile MULTIPART_FILE =
            new MockMultipartFile("tempFileName", BYTE_FILE_REPRESENTATION);

    private static final long EXISTING_USER_ID = 1L;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ImageService imageService;

    @Test
    void uploadImage() throws Exception {
        String expectedResponse = "Image uploaded successfully";
        when(imageService.uploadImage(anyLong(), any())).thenReturn(expectedResponse);

        mvc.perform(MockMvcRequestBuilders.multipart("/api/v1/image")
                        .file(MULTIPART_FILE)
                        .param("userId", String.valueOf(EXISTING_USER_ID))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    void getImage() throws Exception {
        when(imageService.downloadImage(EXISTING_USER_ID)).thenReturn(BYTE_FILE_REPRESENTATION);
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/image/{id}", EXISTING_USER_ID)
                        .accept(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().bytes(BYTE_FILE_REPRESENTATION));
    }

    @Test
    void removeImage() throws Exception {
        doNothing().when(imageService).removeImage(EXISTING_USER_ID);

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/image/{id}", EXISTING_USER_ID))
                .andExpect(status().isOk());
    }
}