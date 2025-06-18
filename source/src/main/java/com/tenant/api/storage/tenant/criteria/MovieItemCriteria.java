package com.tenant.api.storage.tenant.criteria;

import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.model.MovieItem;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class MovieItemCriteria {

    private Long id;
    private String title;
    private String kind;
    private Integer status;

    public Specification<MovieItem> getSpecification() {
        return new Specification<MovieItem>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<MovieItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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

                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
