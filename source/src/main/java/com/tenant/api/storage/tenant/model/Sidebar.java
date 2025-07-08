package com.tenant.api.storage.tenant.model;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.constant.DatabaseConstant;
import com.tenant.api.storage.base.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "side_bar")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Sidebar extends Auditable<String> {

    @Id
    @GenericGenerator(name = BaseConstant.APP_ID_GENERATOR_NAME, strategy = BaseConstant.APP_ID_GENERATOR_STRATEGY)
    @GeneratedValue(generator = BaseConstant.APP_ID_GENERATOR_NAME)
    private Long id;

    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_item_id")
    private MovieItem movieItem;

    @Column(name = "web_thumbnail_url")
    private String webThumbnailUrl;

    @Column(name = "mobile_thumbnail_url")
    private String mobileThumbnailUrl;

    @Column(name = "main_color")
    private String mainColor;

    private Integer ordering;

    private Boolean active = false;
}
