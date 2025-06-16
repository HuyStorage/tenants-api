package com.tenant.api.dto.groupPermission;

import com.tenant.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GroupPermissionDto extends ABasicAdminDto {
    @ApiModelProperty(name = "id") // permissionId
    private Long id;

    @ApiModelProperty(name = "id")
    private String permissionCode;
}
