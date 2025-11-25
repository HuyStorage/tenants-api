package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CollectionRepository extends JpaRepository<Collection, Long>, JpaSpecificationExecutor<Collection> {
    boolean existsByName(String name);
}
