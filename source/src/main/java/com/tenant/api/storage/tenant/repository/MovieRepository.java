package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    Optional<Movie> findFirstBySlug(String slug);

    boolean existsBySlug(String slug);
}
