package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Sidebar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SidebarRepository extends JpaRepository<Sidebar, Long>, JpaSpecificationExecutor<Sidebar> {
    Optional<Sidebar> findByIdAndActive(Long id, Boolean active);

    boolean existsByMovieItemIdAndActive(Long movieId, Boolean active);

    @Modifying
    @Transactional
    @Query("DELETE FROM Sidebar s WHERE s.movieItem.id = :movieItemId")
    void deleteByMovieItemId(@Param("movieItemId") Long movieItemId);

    @Modifying
    @Transactional
    @Query(value = "DELETE s FROM db_side_bar s " +
            "JOIN db_movie_item mi ON s.movie_item_id = mi.id " +
            "WHERE mi.parent_id = :movieItemId", nativeQuery = true)
    void deleteByMovieItemParentId(@Param("movieItemId") Long movieItemId);
}
