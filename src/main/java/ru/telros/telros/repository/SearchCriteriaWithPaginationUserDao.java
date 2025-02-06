package ru.telros.telros.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.telros.telros.model.User;
import ru.telros.telros.util.SearchCriteria;

import java.util.List;

public interface SearchCriteriaWithPaginationUserDao {
    Page<User> findAll(List<SearchCriteria> params, Pageable pageable);
}
