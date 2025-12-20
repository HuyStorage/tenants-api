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
import java.util.List;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "collection")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Collection extends Auditable<String> {
    @Id
    @GenericGenerator(name = BaseConstant.APP_ID_GENERATOR_NAME, strategy = BaseConstant.APP_ID_GENERATOR_STRATEGY)
    @GeneratedValue(generator = BaseConstant.APP_ID_GENERATOR_NAME)
    private Long id;

    private String name;

    private String color;

    private Integer ordering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_id")
    private Style style;

    private Integer type;

    private Boolean randomData = false;

    private String filter;

    @OneToMany(mappedBy = "collection", fetch = FetchType.LAZY)
    @OrderBy("ordering ASC")
    private List<CollectionItem> collectionItems = new ArrayList<>();
}
