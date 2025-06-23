package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Optional<Employee> findFirstByAccountUsernameAndStatusNot(String username, Integer status);

    boolean existsByAccountUsernameAndStatusNot(String username, Integer status);

    boolean existsByAccountEmailAndStatusNot(String email, Integer status);

    boolean existsByAccountPhoneAndStatusNot(String phone, Integer status);
}
