package com.tenant.api.dto.comment;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class AuthorInfoDto {
    private Long id;
    private String email;
    private String fullName;
    private Integer kind;
    private String avatarPath;
    private Integer gender;
}
