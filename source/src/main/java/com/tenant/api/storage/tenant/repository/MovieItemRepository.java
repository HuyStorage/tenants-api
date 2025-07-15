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

    Optional<MovieItem> findByIdAndStatus(Long id, Integer status);

    @Modifying
    @Transactional
    @Query("DELETE FROM MovieItem mi WHERE mi.parent.id = :parentId")
    void deleteByParentId(@Param("parentId") Long parentId);

    boolean existsByMovieId(Long movieId);

    boolean existsByVideoId(Long videoId);

    @Modifying
    @Transactional
    @Query("UPDATE MovieItem mi SET mi.totalEpisode = COALESCE(mi.totalEpisode, 0) + 1 WHERE mi.id = :id")
    void increaseTotalEpisode(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE MovieItem mi SET mi.totalEpisode = mi.totalEpisode - 1 WHERE mi.id = :id AND mi.totalEpisode > 0")
    void decreaseTotalEpisode(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE db_movie_item season " +
            "LEFT JOIN ( " +
            "   SELECT parent_id, COUNT(*) AS total " +
            "   FROM db_movie_item " +
            "   WHERE kind = 2 " +
            "   GROUP BY parent_id " +
            ") AS episode_count ON season.id = episode_count.parent_id " +
            "SET season.total_episode = IFNULL(episode_count.total, 0) " +
            "WHERE season.kind = 1", nativeQuery = true)
    void syncTotalEpisode();
}
