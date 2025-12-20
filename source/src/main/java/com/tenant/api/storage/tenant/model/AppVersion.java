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
@Table(name = DatabaseConstant.PREFIX_TABLE + "app_version")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AppVersion extends Auditable<String> {
    @Id
    @GenericGenerator(name = BaseConstant.APP_ID_GENERATOR_NAME, strategy = BaseConstant.APP_ID_GENERATOR_STRATEGY)
    @GeneratedValue(generator = BaseConstant.APP_ID_GENERATOR_NAME)
    private Long id;
    private Integer code;
    private String name;
    private String filePath;
    private Boolean forceUpdate = false;
    @Column(columnDefinition = "TEXT")
    private String changeLog;
    private Boolean isLatest = false;
}
