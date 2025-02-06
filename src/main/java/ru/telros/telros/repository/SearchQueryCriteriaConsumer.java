package ru.telros.telros.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.telros.telros.model.User;
import ru.telros.telros.util.SearchCriteria;

import java.time.LocalDate;
import java.util.function.Consumer;

@AllArgsConstructor
public class SearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {
    @Getter
    private Predicate predicate;

    private CriteriaBuilder builder;

    private Root root;

    @Override
    public void accept(SearchCriteria param) {
        if (param.getOperation().equalsIgnoreCase(">")) {
            predicate = builder.and(predicate, builder
                    .greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase("<")) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(
                    root.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase(":")) {
            if (root.get(param.getKey()).getJavaType() == String.class) {
                predicate = builder.and(predicate, builder.like(
                        root.get(param.getKey()), "%" + param.getValue() + "%"));
            } else {
                predicate = builder.and(predicate, builder.equal(
                        root.get(param.getKey()), param.getValue()));
            }
        }
    }
}
