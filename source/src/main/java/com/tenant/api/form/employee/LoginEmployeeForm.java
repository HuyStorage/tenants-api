package com.tenant.api.form.employee;

import com.tenant.api.validation.UsernameConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class LoginEmployeeForm {
    @UsernameConstraint
    @ApiModelProperty(required = true)
    private String username;

    @NotBlank(message = "password cannot be empty")
    @ApiModelProperty(required = true)
    private String password;
}
