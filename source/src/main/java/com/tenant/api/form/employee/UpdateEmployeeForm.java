package com.tenant.api.form.employee;

import com.tenant.api.validation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class UpdateEmployeeForm {
    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

    @UsernameConstraint
    @ApiModelProperty(required = true)
    private String username;

    @PhoneConstraint(allowNull = true)
    private String phone;

    @EmailConstraint(allowNull = true)
    private String email;

    @PasswordConstraint(message = "newPassword invalid format", allowNull = true)
    private String newPassword;

    @NotBlank(message = "fullName cant not be empty")
    @ApiModelProperty(required = true)
    private String fullName;

    private String avatarPath;

    @StatusConstraint
    @ApiModelProperty(required = true)
    private Integer status;

    @NotNull(message = "groupId cannot be null")
    @ApiModelProperty(required = true)
    private Long groupId;
}
