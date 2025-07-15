package com.tenant.api.form.employee;

import com.tenant.api.validation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class CreateEmployeeForm {
    @UsernameConstraint
    @ApiModelProperty(required = true)
    private String username;

    @PhoneConstraint(allowNull = true)
    private String phone;

    @EmailConstraint(allowNull = true)
    private String email;

    @PasswordConstraint
    @ApiModelProperty(required = true)
    private String password;

    @NotEmpty(message = "fullName cant not be empty")
    @ApiModelProperty(required = true)
    private String fullName;

    private String avatarPath;

    @StatusConstraint
    @ApiModelProperty(required = true)
    private Integer status;

    @NotNull
    @ApiModelProperty(required = true)
    private Long groupId;
}
