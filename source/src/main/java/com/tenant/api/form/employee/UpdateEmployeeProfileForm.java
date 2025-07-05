package com.tenant.api.form.employee;

import com.tenant.api.validation.EmailConstraint;
import com.tenant.api.validation.PasswordConstraint;
import com.tenant.api.validation.PhoneConstraint;
import com.tenant.api.validation.UsernameConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ApiModel
public class UpdateEmployeeProfileForm {
    @UsernameConstraint
    @ApiModelProperty(required = true)
    private String username;

    @PhoneConstraint
    @ApiModelProperty(required = true)
    private String phone;

    @EmailConstraint(allowNull = true)
    private String email;

    @PasswordConstraint(message = "oldPassword invalid format")
    @ApiModelProperty(required = true)
    private String oldPassword;

    @PasswordConstraint(message = "newPassword invalid format")
    @ApiModelProperty(required = true)
    private String newPassword;

    @NotEmpty(message = "fullName cant not be empty")
    @ApiModelProperty(required = true)
    private String fullName;

    private String avatarPath;
}
