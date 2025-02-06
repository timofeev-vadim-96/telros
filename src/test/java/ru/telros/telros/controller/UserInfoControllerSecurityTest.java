package ru.telros.telros.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;
import ru.telros.telros.config.security.SecurityConfig;
import ru.telros.telros.controller.dto.PhoneNumberDto;
import ru.telros.telros.controller.dto.UserInfoViewDto;
import ru.telros.telros.security.JwtService;
import ru.telros.telros.security.filter.JwtAuthenticationFilter;
import ru.telros.telros.util.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserInfoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtService.class})
@DisplayName("Тест безопасности эндпоинтов контроллера для работы с комментами")
public class UserInfoControllerSecurityTest {
    private static final GrantedAuthority[] USER_ROLES =
            new GrantedAuthority[]{new SimpleGrantedAuthority(Role.ROLE_USER.name())};

    private static final GrantedAuthority[] ADMIN_ROLES =
            new GrantedAuthority[]{new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())};

    private final static String ADMIN_EMAIL = "testAdmin@gmail.com";

    private final static String USER_EMAIL = "user2@example.com";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private static ObjectMapper mapper;

    @MockBean
    private UserInfoController infoController;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeAll
    public static void init() {
        mapper = new ObjectMapper();
    }

    /**
     * Для случаев, когда доступ в метод по url разрешен
     */
    @BeforeEach
    void stubbing() {
        doThrow(new ResponseStatusException(HttpStatus.OK))
                .when(infoController).getAll(any(), any(), any(), any(), any(), any(Pageable.class));
        doThrow(new ResponseStatusException(HttpStatus.OK))
                .when(infoController).get(anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK))
                .when(infoController).delete(anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK))
                .when(infoController).update(any(UserInfoViewDto.class));
        doThrow(new ResponseStatusException(HttpStatus.OK))
                .when(infoController).deletePhoneNumber(anyLong(), anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK))
                .when(infoController).addPhoneNumber(any(PhoneNumberDto.class));
    }

    @DisplayName("should return expected status")
    @ParameterizedTest(name = "{0} {1} for user {4} should return {6} status")
    @MethodSource("getTestData")
    void shouldReturnExpectedStatus(String method, String url, Map<String, String> params, String content,
                                    String userName, GrantedAuthority[] roles, int status) throws Exception {

        MockHttpServletRequestBuilder request = method2RequestBuilder(method, url, params, content);

        if (nonNull(userName)) {
            request = request.with(user(userName).authorities(roles));
        }

        mvc.perform(request)
                .andExpect(status().is(status));
    }

    private MultiValueMap<String, String> convertToMultiValueMap(Map<String, String> params) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(params.size());
        for (String key : params.keySet()) {
            map.add(key, params.get(key));
        }
        return map;
    }

    private MockHttpServletRequestBuilder method2RequestBuilder(
            String method, String url, Map<String, String> params, String content) {
        Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap =
                Map.of("get", MockMvcRequestBuilders::get,
                        "post", MockMvcRequestBuilders::post,
                        "put", MockMvcRequestBuilders::put,
                        "delete", MockMvcRequestBuilders::delete,
                        "patch", MockMvcRequestBuilders::patch);
        return methodMap.get(method)
                .apply(url)
                .params(convertToMultiValueMap(params))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    private static Stream<Arguments> getTestData() throws JsonProcessingException {
        List<Arguments> args = new ArrayList<>();
        addArgsForGet(args);
        addArgsForGetAll(args);
        addArgsForUpdate(args);
        addArgsForDelete(args);
        addArgsForAddPhoneNumber(args);
        addArgsForDeletePhoneNumber(args);

        return args.stream();
    }

    private static void addArgsForGet(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/api/v1/user/1",
                        Map.of(), Strings.EMPTY, USER_EMAIL, USER_ROLES, 200),
                Arguments.of("get", "/api/v1/user/1",
                        Map.of(), Strings.EMPTY, ADMIN_EMAIL, ADMIN_ROLES, 200),
                Arguments.of("get", "/api/v1/user/1",
                        Map.of(), Strings.EMPTY, null, null, 403)));
    }

    private static void addArgsForGetAll(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/api/v1/user",
                        Map.of(), Strings.EMPTY, USER_EMAIL, USER_ROLES, 403),
                Arguments.of("get", "/api/v1/user",
                        Map.of(), Strings.EMPTY, ADMIN_EMAIL, ADMIN_ROLES, 200),
                Arguments.of("get", "/api/v1/user",
                        Map.of(), Strings.EMPTY, null, null, 403)));
    }

    private static void addArgsForUpdate(List<Arguments> args) throws JsonProcessingException {
        UserInfoViewDto dto = UserInfoViewDto.builder()
                .id(1L)
                .firstName("updated first name")
                .secondName("updated second name")
                .patronymic("updated patronymic")
                .build();
        String json = mapper.writeValueAsString(dto);

        args.addAll(List.of(
                Arguments.of("patch", "/api/v1/user",
                        Map.of(), json, USER_EMAIL, USER_ROLES, 200),
                Arguments.of("patch", "/api/v1/user",
                        Map.of(), json, ADMIN_EMAIL, ADMIN_ROLES, 200),
                Arguments.of("patch", "/api/v1/user",
                        Map.of(), json, null, null, 403)));
    }

    private static void addArgsForDelete(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("delete", "/api/v1/user/1",
                        Map.of(), Strings.EMPTY, USER_EMAIL, USER_ROLES, 200),
                Arguments.of("delete", "/api/v1/user/1",
                        Map.of(), Strings.EMPTY, ADMIN_EMAIL, ADMIN_ROLES, 200),
                Arguments.of("delete", "/api/v1/user/1",
                        Map.of(), Strings.EMPTY, null, null, 403)));
    }

    private static void addArgsForAddPhoneNumber(List<Arguments> args) throws JsonProcessingException {
        PhoneNumberDto phoneNumberDto = PhoneNumberDto.builder()
                .phoneNumber("82223334455")
                .userId(1L)
                .build();
        String json = mapper.writeValueAsString(phoneNumberDto);

        args.addAll(List.of(
                Arguments.of("post", "/api/v1/user/phone",
                        Map.of(), json, USER_EMAIL, USER_ROLES, 200),
                Arguments.of("post", "/api/v1/user/phone",
                        Map.of(), json, ADMIN_EMAIL, ADMIN_ROLES, 200),
                Arguments.of("post", "/api/v1/user/phone",
                        Map.of(), json, null, null, 403)));
    }

    private static void addArgsForDeletePhoneNumber(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("delete", "/api/v1/user/1/phone/1",
                        Map.of(), Strings.EMPTY, USER_EMAIL, USER_ROLES, 200),
                Arguments.of("delete", "/api/v1/user/1/phone/1",
                        Map.of(), Strings.EMPTY, ADMIN_EMAIL, ADMIN_ROLES, 200),
                Arguments.of("delete", "/api/v1/user/1/phone/1",
                        Map.of(), Strings.EMPTY, null, null, 403)));
    }
}
