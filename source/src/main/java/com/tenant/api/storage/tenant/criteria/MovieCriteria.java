package com.tenant.api.storage.tenant.criteria;

import com.tenant.api.storage.tenant.model.Category;
import com.tenant.api.storage.tenant.model.Movie;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class MovieCriteria {
    private Long id;
    private String title;
    private String originalTitle;
    private Integer type;
    private Integer ageRating;
    private Integer status;
    private String language;
    private String country;
    private Boolean isFeatured;
    private List<Long> categoryIds;

    public Specification<Movie> getSpecification() {
        return new Specification<Movie>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Movie> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }

                if (getTitle() != null) {
                    predicates.add(cb.like(cb.lower(root.get("title")), "%" + getTitle().toLowerCase() + "%"));
                }

                if (getOriginalTitle() != null) {
                    predicates.add(cb.like(cb.lower(root.get("originalTitle")), "%" + getOriginalTitle().toLowerCase() + "%"));
                }

                if (getType() != null) {
                    predicates.add(cb.equal(root.get("type"), getType()));
                }

                if (getAgeRating() != null) {
                    predicates.add(cb.equal(root.get("ageRating"), getAgeRating()));
                }

                if (getLanguage() != null) {
                    predicates.add(cb.equal(root.get("language"), getLanguage()));
                }

                if (getCountry() != null) {
                    predicates.add(cb.equal(root.get("country"), getCountry()));
                }

                if (getIsFeatured() != null) {
                    predicates.add(cb.equal(root.get("isFeatured"), getIsFeatured()));
                }

                if (getCategoryIds() != null && !getCategoryIds().isEmpty()) {
                    Join<Movie, Category> categoryJoin = root.join("categories", JoinType.INNER);
                    predicates.add(categoryJoin.get("id").in(getCategoryIds()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
