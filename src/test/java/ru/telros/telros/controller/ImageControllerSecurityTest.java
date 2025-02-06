package ru.telros.telros.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;
import ru.telros.telros.config.security.SecurityConfig;
import ru.telros.telros.security.JwtService;
import ru.telros.telros.security.filter.JwtAuthenticationFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ImageController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@DisplayName("Тест безопасности эндпоинтов контроллера для работы с комментами")
public class ImageControllerSecurityTest {
    private final static String ADMIN_EMAIL = "testAdmin@gmail.com";

    private final static String USER_EMAIL = "user2@example.com";

    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ImageController imageController;

    @MockBean
    private UserDetailsService userDetailsService;

    /**
     * Для случаев, когда доступ в метод по url разрешен
     */
    @BeforeEach
    void stubbing() {
        doThrow(new ResponseStatusException(HttpStatus.OK))
                .when(imageController).getImage(anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK))
                .when(imageController).uploadImage(any(), anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK))
                .when(imageController).removeImage(anyLong());
    }

    @Test
    @WithMockUser(username = USER_EMAIL, authorities = "ROLE_USER")
    void getImageByUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/image/{id}", USER_ID)
                        .accept(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, authorities = "ROLE_ADMIN")
    void getImageByAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/image/{id}", USER_ID)
                        .accept(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void getImageByUnknown() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/v1/image/{id}", USER_ID)
                        .accept(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USER_EMAIL, authorities = "ROLE_USER")
    void uploadImageByUser() throws Exception {
        final MockMultipartFile MULTIPART_FILE =
                new MockMultipartFile("tempFileName", "FILE".getBytes());

        mvc.perform(MockMvcRequestBuilders.multipart("/api/v1/image")
                        .file(MULTIPART_FILE)
                        .param("userId", String.valueOf(USER_ID))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, authorities = "ROLE_ADMIN")
    void uploadImageByAdmin() throws Exception {
        final MockMultipartFile MULTIPART_FILE =
                new MockMultipartFile("tempFileName", "FILE".getBytes());

        mvc.perform(MockMvcRequestBuilders.multipart("/api/v1/image")
                        .file(MULTIPART_FILE)
                        .param("userId", String.valueOf(USER_ID))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void uploadImageByUnknown() throws Exception {
        final MockMultipartFile MULTIPART_FILE =
                new MockMultipartFile("tempFileName", "FILE".getBytes());

        mvc.perform(MockMvcRequestBuilders.multipart("/api/v1/image")
                        .file(MULTIPART_FILE)
                        .param("userId", String.valueOf(USER_ID))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, authorities = "ROLE_ADMIN")
    void removeImageByAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/image/{id}", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = USER_EMAIL, authorities = "ROLE_USER")
    void removeImageByUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/image/{id}", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void removeImageByUnknown() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/image/{id}", USER_ID))
                .andExpect(status().isForbidden());
    }
}
