package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.MoviePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MoviePersonRepository extends JpaRepository<MoviePerson, Long>, JpaSpecificationExecutor<MoviePerson> {
    List<MoviePerson> findAllByMovieId(Long movieId);

    boolean existsByPersonId(Long id);

    @Modifying
    @Transactional
    @Query("DELETE MoviePerson mp where mp.movie.id = :movieId")
    void deleteByMovieId(Long movieId);

    @Modifying
    @Transactional
    @Query("DELETE MoviePerson mp WHERE mp.movie.id = :movieId AND mp.person.id NOT IN :personIds")
    void deleteByMovieIdAndPersonIdNotIn(@Param("movieId") Long movieId, @Param("personIds") List<Long> personIds);
}
