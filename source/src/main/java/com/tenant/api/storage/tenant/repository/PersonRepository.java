package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Person;
import com.tenant.api.storage.tenant.model.VideoLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    Optional<Person> findByIdAndStatus(Long id, Integer status);
}
