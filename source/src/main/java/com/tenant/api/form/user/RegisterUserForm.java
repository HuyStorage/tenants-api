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
public class RegisterUserForm {
    @UsernameConstraint
    @ApiModelProperty(required = true)
    private String username;

    @EmailConstraint(allowNull = true)
    private String email;

    @PasswordConstraint
    private String password;

}
