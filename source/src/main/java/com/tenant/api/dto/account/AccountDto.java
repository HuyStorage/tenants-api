package com.tenant.api.dto.account;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class AccountDto {
    private Long id;
    private String fullName;
    private String avatarPath;
    private Integer kind;
}
