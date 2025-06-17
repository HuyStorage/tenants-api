package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findFirstByUsernameAndStatusNot(String username, Integer status);

    boolean existsByUsernameAndStatusNot(String username, Integer status);

    boolean existsByEmailAndStatusNot(String email, Integer status);

    boolean existsByPhoneAndStatusNot(String phone, Integer status);
}
