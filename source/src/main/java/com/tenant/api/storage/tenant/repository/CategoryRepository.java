package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByIdAndStatus(Long id, Integer status);

    boolean existsByName(String name);
}
