package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.MovieItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MovieItemRepository extends JpaRepository<MovieItem, Long>, JpaSpecificationExecutor<MovieItem> {

    @Query("SELECT MAX(mi.ordering) FROM MovieItem mi WHERE mi.parent IS NULL AND mi.movie.id = :movieId")
    Integer findMaxOrderingForSeason(@Param("movieId") Long movieId);

    @Query("SELECT MAX(mi.ordering) FROM MovieItem mi WHERE mi.parent.id = :parentId AND mi.movie.id = :movieId")
    Integer findMaxOrdering(@Param("parentId") Long parentId, @Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MovieItem mi WHERE mi.parent.id = :parentId")
    void deleteByParentId(@Param("parentId") Long parentId);

    boolean existsByMovieId(Long movieId);
}
