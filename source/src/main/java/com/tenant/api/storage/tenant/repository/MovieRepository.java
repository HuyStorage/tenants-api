package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    Optional<Movie> findFirstBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByCategories_Id(Long categoryId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM db_movie_category WHERE movie_id = :movieId", nativeQuery = true)
    void deleteMovieCategory(@Param("movieId") Long movieId);
}
