package com.tenant.api.dto.group;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.groupPermission.GroupPermissionDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel
public class GroupDto extends ABasicAdminDto {
    @ApiModelProperty(name = "name")
    private String name;
    @ApiModelProperty(name = "description")
    private String description;
    @ApiModelProperty(name = "kind")
    private int kind;
    @ApiModelProperty(name = "permissions")
    private List<GroupPermissionDto> permissions;
    @ApiModelProperty(name = "isSystemRole")
    private Boolean isSystemRole;
}
