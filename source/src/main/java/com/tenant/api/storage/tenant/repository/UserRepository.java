package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findFirstByAccountUsernameAndStatusNot(String username, Integer status);

    Optional<User> findFirstByAccountEmailAndStatusNot(String email, Integer status);

    boolean existsByAccountUsernameAndStatusNot(String username, Integer status);

    boolean existsByAccountEmailAndStatusNot(String email, Integer status);

    boolean existsByAccountPhoneAndStatusNot(String phone, Integer status);
}
