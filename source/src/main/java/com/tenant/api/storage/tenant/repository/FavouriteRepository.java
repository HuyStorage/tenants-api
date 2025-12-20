package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FavouriteRepository extends JpaRepository<Favourite, Long>, JpaSpecificationExecutor<Favourite> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Favourite f WHERE f.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favourite f WHERE f.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Favourite f WHERE f.person.id = :personId")
    void deleteByPersonId(@Param("personId") Long personId);

    @Query(
            "SELECT f FROM Favourite f " +
                    "WHERE f.user.id = :userId " +
                    "AND f.type = :type " +
                    "AND ( " +
                    "   (:type = 1 AND f.movie.id = :targetId) " +
                    "   OR (:type = 2 AND f.person.id = :targetId) " +
                    ")"
    )
    Optional<Favourite> findByUserIdAndTypeAndTargetId(@Param("userId") Long userId,
                                                       @Param("type") Integer type,
                                                       @Param("targetId") Long targetId);
}
