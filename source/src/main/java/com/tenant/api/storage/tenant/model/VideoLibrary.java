package com.tenant.api.storage.tenant.model;

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
@Table(name = DatabaseConstant.PREFIX_TABLE + "video_library")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class VideoLibrary extends Auditable<String> {

    private String name;

    @Column(name = "original_url")
    private String originalUrl;

    @Column(name = "hls_url")
    private String hlsUrl;
}
