package com.example.trainstationtemp.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.regex.Pattern;

public class TrainstationSpecification<T> implements Specification<T> {
    protected final SearchCriteria criteria;
    private static final Pattern pattern = Pattern.compile("(\\w+)([<:>])(.*)");

    public TrainstationSpecification(Optional<String> filter) {
        criteria = filter.map(f -> {
            var match = pattern.matcher(f);
            return match.matches()
                    ? new SearchCriteria(match.group(1), match.group(2), match.group(3))
                    : new SearchCriteria("", "", null);
        }).orElseGet(() -> new SearchCriteria("", "", null));
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (criteria instanceof SearchCriteria(String key, String operation, Object value)) {
            return switch (operation) {
                case ">"-> builder
                        .greaterThanOrEqualTo(root.get(key),value.toString());
                case "<" -> builder
                        .lessThanOrEqualTo(root.get(key), value.toString());
                case ":" -> (root.get(key).getJavaType() == String.class)
                                ? builder.like(root.get(key), "%" + value + "%")
                                : builder.equal(root.get(key), value);
                default -> null;
            };
        }
        return null;
    }
}
