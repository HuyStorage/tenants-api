package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.totalLike = r.totalLike + 1 WHERE r.id = :id")
    void increaseTotalLike(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.totalLike = r.totalLike - 1 WHERE r.id = :id AND r.totalLike > 0")
    void decreaseTotalLike(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.totalDislike = r.totalDislike + 1 WHERE r.id = :id")
    void increaseTotalDislike(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Review r SET r.totalDislike = r.totalDislike - 1 WHERE r.id = :id AND r.totalDislike > 0")
    void decreaseTotalDislike(@Param("id") Long id);
}
