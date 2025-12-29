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

    Optional<MovieItem> findByIdAndKindNot(Long id, Integer kind);

    @Query("SELECT mi FROM MovieItem mi LEFT JOIN FETCH mi.parent WHERE mi.movie.id = :movieId AND mi.status = :status")
    List<MovieItem> findByMovieIdAndStatusWithParent(@Param("movieId") Long movieId, @Param("status") Integer status);

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

    @Modifying
    @Transactional
    @Query("UPDATE MovieItem mi SET mi.video = null WHERE mi.video.id = :videoId")
    void detachVideoFromMovieItem(@Param("videoId") Long videoId);

    @Modifying
    @Transactional
    @Query("DELETE MovieItem mi WHERE mi.movie.id = :movieId AND mi.kind = :kind")
    void deleteByMovieIdAndKind(@Param("movieId") Long movieId, @Param("kind") Integer kind);

    @Query("SELECT mi.thumbnailUrl FROM MovieItem mi WHERE mi.movie.id = :movieId AND mi.thumbnailUrl IS NOT NULL ")
    List<String> findThumbnailsByMovieId(@Param("movieId") Long movieId);

    Long countByMovieIdAndKind(Long movieId, Integer kind);

    @Query("SELECT mi.id FROM MovieItem mi WHERE mi.parent.id = :parentId AND mi.kind != :kind")
    List<Long> findIdByParentIdAndKindNot(@Param("parentId") Long parentId, @Param("kind") Integer kind);

    @Query("SELECT MAX(mi.ordering) " +
            "FROM MovieItem mi " +
            "WHERE mi.movie.id = :movieId " +
            "AND (" +
            "   (:kind = 1 AND mi.parent IS NULL) " +
            "   OR (:kind != 1 AND mi.parent.id = :parentId)" +
            ")")
    Optional<Integer> findMaxOrdering(@Param("movieId") Long movieId, @Param("kind") Integer kind, @Param("parentId") Long parentId);
}
