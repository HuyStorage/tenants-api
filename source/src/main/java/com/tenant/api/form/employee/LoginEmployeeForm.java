package com.tenant.api.form.employee;

import com.tenant.api.validation.PasswordConstraint;
import com.tenant.api.validation.UsernameConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class LoginEmployeeForm {
    @UsernameConstraint
    @ApiModelProperty(required = true)
    private String username;

    @PasswordConstraint
    @ApiModelProperty(required = true)
    private String password;
}
