package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long>, JpaSpecificationExecutor<CollectionItem> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CollectionItem ci WHERE ci.collection.id = :collectionId")
    void deleteByCollectionId(@Param("collectionId") Long collectionId);

    @Transactional
    @Modifying
    @Query("DELETE FROM CollectionItem ci WHERE ci.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);

    boolean existsByCollectionIdAndMovieId(Long collectionId, Long movieId);

    int countByCollectionId(Long collectionId);

    @Query("SELECT MAX(ci.ordering) FROM CollectionItem ci WHERE ci.collection.id = :collectionId")
    Optional<Integer> findMaxOrdering(@Param("collectionId") Long collectionId);
}
