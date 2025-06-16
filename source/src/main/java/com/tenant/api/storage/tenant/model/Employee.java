package com.tenant.api.storage.tenant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tenant.api.constant.DatabaseConstant;
import com.tenant.api.storage.base.Auditable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Entity
@Table(name = DatabaseConstant.PREFIX_TABLE + "employee")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Employee extends Auditable<String> {

    private String username;

    private Integer kind;

    private String phone;

    private String email;

    @JsonIgnore
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar_path")
    private String avatarPath;
}
