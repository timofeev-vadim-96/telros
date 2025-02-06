package ru.telros.telros.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.telros.telros.model.User;
import ru.telros.telros.util.SearchCriteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Кастомный репозиторий для получения юзаков с пагинацией и фильтрацией по критериям")
@DataJpaTest
@Import(SearchCriteriaWithPaginationUserDaoImpl.class)
class SearchCriteriaWithPaginationUserDaoImplTest {
    @Autowired
    private SearchCriteriaWithPaginationUserDao criteriaDao;

    @ParameterizedTest
    @MethodSource("getArguments")
    void findAll(SearchCriteria criteria, int expectedResultSize) {
        Pageable pageable = PageRequest.of(0, 10);
        var criteriaList = new ArrayList<>(List.of(criteria));
        Page<User> users = criteriaDao.findAll(criteriaList, pageable);

        assertEquals(expectedResultSize, users.getTotalElements());
        assertEquals(1, users.getTotalPages());
    }

    private static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(
                        new SearchCriteria("firstName", ":", "Admin"), 1),
                Arguments.of(
                        new SearchCriteria("firstName", ":", "User"), 9),
                Arguments.of(
                        new SearchCriteria("secondName", ":", "Second"), 1),
                Arguments.of(
                        new SearchCriteria("patronymic", ":", "Adminovich"), 1),
                Arguments.of(
                        new SearchCriteria("patronymic", ":", "Userovich"), 9),
                Arguments.of(
                        new SearchCriteria("birthDay", ":", LocalDate.of(1980, 1, 15)), 1),
                Arguments.of(
                        new SearchCriteria("phoneNumber", ":", "81199999999"), 1));
    }
}