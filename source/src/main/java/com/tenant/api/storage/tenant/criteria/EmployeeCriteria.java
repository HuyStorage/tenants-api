package com.tenant.api.storage.tenant.criteria;

import com.tenant.api.storage.tenant.model.Employee;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class EmployeeCriteria {

    private Long id;
    private String username;
    private Long kind;
    private String fullName;
    private String phone;
    private Integer status;

    public Specification<Employee> getSpecification() {
        return new Specification<Employee>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }

                if (getKind() != null) {
                    predicates.add(cb.equal(root.get("kind"), getKind()));
                }

                if (getStatus() != null) {
                    predicates.add(cb.equal(root.get("status"), getStatus()));
                }

                if (getUsername() != null) {
                    predicates.add(cb.like(cb.lower(root.get("username")), "%" + getUsername().toLowerCase() + "%"));
                }

                if (getFullName() != null) {
                    predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + getFullName().toLowerCase() + "%"));
                }

                if (getPhone() != null) {
                    predicates.add(cb.like(cb.lower(root.get("phone")), "%" + getPhone().toLowerCase() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
