package com.tenant.api.storage.tenant.model;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.constant.DatabaseConstant;
import com.tenant.api.storage.base.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "movie_item")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MovieItem extends Auditable<String> {
    @Id
    @GenericGenerator(name = BaseConstant.APP_ID_GENERATOR_NAME, strategy = BaseConstant.APP_ID_GENERATOR_STRATEGY)
    @GeneratedValue(generator = BaseConstant.APP_ID_GENERATOR_NAME)
    private Long id;

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private Integer kind; // 1: season, 2: episode, 3: trailer

    private String label;

    private Integer ordering;

    // Season  → parent = null
    // Episode → parent = season
    // Trailer → parent = episode (if movie is type SERIES) or null (if movie is type SINGLE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MovieItem parent; // season -> episode -> trailer

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<MovieItem> children;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private VideoLibrary video;

    @Column(name = "release_date")
    private Date releaseDate;

    @Column(name = "total_episode")
    private Integer totalEpisode = 0;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
}
