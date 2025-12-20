package com.tenant.api.storage.tenant.model;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.constant.DatabaseConstant;
import com.tenant.api.storage.base.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "comment")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Comment extends Auditable<String> {
    @Id
    @GenericGenerator(name = BaseConstant.APP_ID_GENERATOR_NAME, strategy = BaseConstant.APP_ID_GENERATOR_STRATEGY)
    @GeneratedValue(generator = BaseConstant.APP_ID_GENERATOR_NAME)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_item_id")
    private MovieItem movieItem;

    private Long movieId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer totalLike = 0;

    private Integer totalDislike = 0;

    private Integer totalChildren = 0;

    private Boolean isPinned = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(name = "account_id")
    private Long authorId;

    @Column(columnDefinition = "TEXT")
    private String authorInfo;

    private Long replyToId;

    @Column(columnDefinition = "TEXT")
    private String replyToInfo;
}
