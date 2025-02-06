package ru.telros.telros.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.telros.telros.model.PhoneNumber;
import ru.telros.telros.model.User;
import ru.telros.telros.util.SearchCriteria;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SearchCriteriaWithPaginationUserDaoImpl implements SearchCriteriaWithPaginationUserDao {
    @PersistenceContext
    private EntityManager entityManager;

    public Page<User> findAll(List<SearchCriteria> params, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root root = query.from(User.class);

        Predicate predicate = getPredicate(params, builder, root);

        query.where(predicate);
        TypedQuery<User> typedQuery = entityManager.createQuery(query);
        // Добавление пагинации
        return performPagination(params, pageable, typedQuery);
    }

    private static Predicate getPredicate(List<SearchCriteria> params, CriteriaBuilder builder, Root root) {
        Predicate predicate = builder.conjunction();

        List<SearchCriteria> otherParams = new ArrayList<>();

        //если нужно фильтровать по номеру телефона
        for (SearchCriteria param : params) {
            if (param.getKey().equals("phoneNumber")) {
                Join<User, PhoneNumber> phones = root.join("phoneNumbers", JoinType.INNER);
                predicate = builder.and(
                        predicate,
                        builder.like(phones.get("phoneNumber"), "%" + param.getValue() + "%"));
            } else {
                otherParams.add(param);
            }
        }

        SearchQueryCriteriaConsumer searchConsumer =
                new SearchQueryCriteriaConsumer(predicate, builder, root);
        otherParams.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        return predicate;
    }

    private PageImpl<User> performPagination(List<SearchCriteria> params, Pageable pageable, TypedQuery<User> typedQuery) {
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<User> result = typedQuery.getResultList();
        long total = countTotal(params);

        return new PageImpl<>(result, pageable, total);
    }

    /**
     * Метод для подсчета общего количества записей в запросе
     *
     * @param params критерии поиска
     * @return общее количество записей
     */
    private long countTotal(List<SearchCriteria> params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root root = countQuery.from(User.class);

        Predicate predicate = getPredicate(params, builder, root);
        countQuery.where(predicate);
        countQuery.select(builder.count(root));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
