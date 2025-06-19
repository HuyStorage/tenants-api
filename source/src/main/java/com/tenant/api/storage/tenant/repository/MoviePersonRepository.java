package com.tenant.api.storage.tenant.repository;

import com.tenant.api.storage.tenant.model.MoviePerson;
import com.tenant.api.storage.tenant.model.VideoLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MoviePersonRepository extends JpaRepository<MoviePerson, Long>, JpaSpecificationExecutor<MoviePerson> {
}
