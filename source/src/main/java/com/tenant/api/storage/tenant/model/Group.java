package com.tenant.api.storage.tenant.model;

import com.tenant.api.constant.DatabaseConstant;
import com.tenant.api.storage.base.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "group")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Group extends Auditable<String> {
    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "kind")
    private int kind;

    @Column(name = "is_system_role")
    private Boolean isSystemRole = false;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupPermission> permissions = new ArrayList<>();
}
