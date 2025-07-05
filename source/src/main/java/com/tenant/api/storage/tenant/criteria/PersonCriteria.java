package com.tenant.api.storage.tenant.criteria;

import com.tenant.api.storage.tenant.model.Person;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class PersonCriteria {

    private Long id;
    private String name;
    private String otherName;
    private Integer gender;
    private Integer kind;
    private Integer status;

    public Specification<Person> getSpecification() {
        return new Specification<Person>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if (getName() != null) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
                }

                if (getOtherName() != null) {
                    predicates.add(cb.like(cb.lower(root.get("otherName")), "%" + getOtherName().toLowerCase() + "%"));
                }

                if (getGender() != null) {
                    predicates.add(cb.equal(root.get("gender"), getGender()));
                }

                if (getKind() != null) {
                    Join<Person, Integer> kindJoin = root.join("kinds");
                    predicates.add(cb.equal(kindJoin, kind));
                }

                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
