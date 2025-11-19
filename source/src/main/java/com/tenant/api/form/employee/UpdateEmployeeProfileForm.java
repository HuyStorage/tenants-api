package com.tenant.api.form.employee;

import com.tenant.api.validation.EmailConstraint;
import com.tenant.api.validation.PasswordConstraint;
import com.tenant.api.validation.PhoneConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class UpdateEmployeeProfileForm {
    @PhoneConstraint(allowNull = true)
    private String phone;

    @EmailConstraint(allowNull = true)
    private String email;

    private String oldPassword;

    @PasswordConstraint(message = "newPassword invalid format", allowNull = true)
    private String newPassword;

    @NotBlank(message = "fullName cant not be empty")
    @ApiModelProperty(required = true)
    private String fullName;

    private String avatarPath;
}
