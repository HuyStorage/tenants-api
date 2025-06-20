package com.tenant.api.storage.tenant.model;

import com.tenant.api.constant.DatabaseConstant;
import com.tenant.api.storage.base.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "movie_person")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MoviePerson extends Auditable<String> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    private Integer kind; // 1: DIRECTOR, 2: ACTOR

    // kind = 1 -> null
    @Column(name = "character_name")
    private String characterName;

    private Integer ordering;

}
