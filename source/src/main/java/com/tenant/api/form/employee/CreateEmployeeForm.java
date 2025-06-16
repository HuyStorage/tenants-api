package com.tenant.api.form.employee;

import com.tenant.api.validation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ApiModel
public class CreateEmployeeForm {
    @UsernameConstraint
    @ApiModelProperty(required = true)
    private String username;

    @EmployeeKind
    @ApiModelProperty(required = true)
    private Integer kind;

    @PhoneConstraint(allowNull = true)
    @ApiModelProperty(name = "phone")
    private String phone;

    @EmailConstraint(allowNull = true)
    @ApiModelProperty(name = "email")
    private String email;

    @NotEmpty(message = "password cant not be empty")
    @Size(min = 6, message = "password must be at least 6 characters")
    @ApiModelProperty(name = "password", required = true)
    private String password;

    @NotEmpty(message = "fullName cant not be empty")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;

    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;

    @StatusConstraint
    @ApiModelProperty(name = "status", required = true)
    private Integer status;

    @NotNull
    @ApiModelProperty(name = "groupId", required = true)
    private Long groupId;
}
