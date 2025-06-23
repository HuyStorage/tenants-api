package com.tenant.api.dto.user;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.group.GroupDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UserDto extends ABasicAdminDto {
    @ApiModelProperty(name = "id")
    private Long id;

    @ApiModelProperty(name = "kind")
    private int kind;

    @ApiModelProperty(name = "username")
    private String username;

    @ApiModelProperty(name = "phone")
    private String phone;

    @ApiModelProperty(name = "email")
    private String email;

    @ApiModelProperty(name = "fullName")
    private String fullName;

    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;

    @ApiModelProperty(name = "status")
    private Integer status;

    @ApiModelProperty(name = "group")
    private GroupDto group;
}
