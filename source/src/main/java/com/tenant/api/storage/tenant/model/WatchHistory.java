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
@Table(
        name = DatabaseConstant.PREFIX_TABLE + "watch_history",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_watch_history_user_movie_item",
                        columnNames = {"user_id", "movie_item_id"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class WatchHistory extends Auditable<String> {
    @Id
    @GenericGenerator(name = BaseConstant.APP_ID_GENERATOR_NAME, strategy = BaseConstant.APP_ID_GENERATOR_STRATEGY)
    @GeneratedValue(generator = BaseConstant.APP_ID_GENERATOR_NAME)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_item_id")
    private MovieItem movieItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long lastWatchSeconds;

    private Boolean isCompleted = false;

    private Integer timesWatched = 0;
}
