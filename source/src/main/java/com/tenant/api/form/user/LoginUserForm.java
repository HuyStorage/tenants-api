package com.tenant.api.form.user;

import com.tenant.api.validation.EmailConstraint;
import com.tenant.api.validation.PasswordConstraint;
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
public class LoginUserForm {
    @EmailConstraint
    @ApiModelProperty(required = true)
    private String email;

    @PasswordConstraint
    @ApiModelProperty(name = "password", required = true)
    private String password;
}
