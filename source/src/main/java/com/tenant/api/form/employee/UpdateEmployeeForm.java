package com.tenant.api.form.employee;

import com.tenant.api.validation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

    @EmployeeKind
    @ApiModelProperty(required = true)
    private Integer kind;

    @PhoneConstraint
    @ApiModelProperty(name = "phone", required = true)
    private String phone;

    @EmailConstraint(allowNull = true)
    @ApiModelProperty(name = "email")
    private String email;

    @Size(min = 6, message = "oldPassword must be at least 6 characters")
    @ApiModelProperty(name = "oldPassword")
    private String oldPassword;

    @Size(min = 6, message = "newPassword must be at least 6 characters")
    @ApiModelProperty(name = "newPassword")
    private String newPassword;

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
