package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    Optional<Account> findFirstByUsernameAndStatusNot(String username, Integer status);

    boolean existsByUsernameAndStatusNot(String username, Integer status);

    boolean existsByEmailAndStatusNot(String email, Integer status);

    boolean existsByPhoneAndStatusNot(String phone, Integer status);
}
