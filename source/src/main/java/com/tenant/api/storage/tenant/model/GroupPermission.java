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
@Table(name = DatabaseConstant.PREFIX_TABLE + "group_permission")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class GroupPermission extends Auditable<String> {

    @Id
    @GenericGenerator(name = BaseConstant.APP_ID_GENERATOR_NAME, strategy = BaseConstant.APP_ID_GENERATOR_STRATEGY)
    @GeneratedValue(generator = BaseConstant.APP_ID_GENERATOR_NAME)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "p_code", nullable = false)
    private String permissionCode;
}
