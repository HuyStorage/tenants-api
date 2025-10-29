package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long>, JpaSpecificationExecutor<Group> {
    Optional<Group> findByIdAndStatus(Long id, Integer status);

    @Query("SELECT COALESCE(MAX(g.kind), 99) + 1 FROM Group g")
    int getNextKind();

    boolean existsByName(String name);
}
