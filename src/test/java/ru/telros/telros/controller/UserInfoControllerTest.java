package ru.telros.telros.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.controller.dto.UserInfoViewDto;
import ru.telros.telros.security.filter.JwtAuthenticationFilter;
import ru.telros.telros.service.UserInfoService;
import ru.telros.telros.service.dto.UserInfoDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserInfoController.class},
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class))
@DisplayName("Контроллер для работы с изображениями")
class UserInfoControllerTest {
    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    @MockBean
    private UserInfoService infoService;

    private UserInfoDto infoDto;

    private UserInfoViewDto viewDto;

    @BeforeEach
    void setUp() {
        infoDto = UserInfoDto.builder()
                .id(USER_ID)
                .firstName("name")
                .secondName("secondName")
                .patronymic("patronymic")
                .birthDay(LocalDate.now())
                .email("email@ya.ru")
                .phoneNumbers(Set.of())
                .build();
        viewDto = UserInfoViewDto.builder()
                .id(infoDto.getId())
                .firstName(infoDto.getFirstName())
                .secondName(infoDto.getSecondName())
                .patronymic(infoDto.getPatronymic())
                .birthDay(infoDto.getBirthDay())
                .build();
    }

    @Test
    void get() throws Exception {
        when(infoService.get(USER_ID)).thenReturn(infoDto);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user/{id}", USER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(infoDto)));
    }

    @Test
    void getAll() throws Exception {
        PageImpl<UserInfoDto> users = new PageImpl<>(
                List.of(infoDto),
                PageRequest.of(1, 1),
                1L);
        when(infoService.getAll(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(users);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(users)));
    }

    @Test
    void update() throws Exception {
        when(infoService.update(any(UserInfoViewDto.class))).thenReturn(infoDto);

        mvc.perform(MockMvcRequestBuilders.patch("/api/v1/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(viewDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(infoDto)));
    }

    @Test
    void delete() throws Exception {
        doNothing().when(infoService).deleteById(USER_ID);

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/{id}", USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void deletePhoneNumber() throws Exception {
        long phoneNumberId = 1L;
        when(infoService.deletePhoneNumber(USER_ID, phoneNumberId)).thenReturn(infoDto);

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/user/{userId}/phone/{phoneNumberId}",
                                USER_ID, phoneNumberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(infoDto)));
    }

    @Test
    void addPhoneNumber() throws Exception {
        final String number = "+7 (111) 111-11-11";
        PhoneNumberDto phoneNumberDto = new PhoneNumberDto(1L, number, USER_ID);
        when(infoService.addPhoneNumber(any(PhoneNumberDto.class))).thenReturn(infoDto);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/user/phone")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(phoneNumberDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(infoDto)));
    }
}