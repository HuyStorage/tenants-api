package com.tenant.api.form.employee;

import com.tenant.api.validation.UsernameConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@ApiModel
public class LoginEmployeeForm {
    @UsernameConstraint
    @ApiModelProperty(required = true)
    private String username;

    @NotEmpty(message = "password cant not be empty")
    @Size(min = 6, message = "password must be at least 6 characters")
    @ApiModelProperty(name = "password", required = true)
    private String password;
}
