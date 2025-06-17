package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.GroupPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupPermissionRepository extends JpaRepository<GroupPermission, Long>, JpaSpecificationExecutor<GroupPermission> {
}
