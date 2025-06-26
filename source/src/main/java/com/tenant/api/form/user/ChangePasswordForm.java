package com.tenant.api.form.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@ApiModel
public class ChangePasswordForm {
        @Size(min = 6, message = "oldPassword must be at least 6 characters")
    @ApiModelProperty(name = "oldPassword")
    private String oldPassword;

    @Size(min = 6, message = "newPassword must be at least 6 characters")
    @ApiModelProperty(name = "newPassword")
    private String newPassword;
}
