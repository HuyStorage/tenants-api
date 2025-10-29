package com.tenant.api.form.user;

import com.tenant.api.validation.EmailConstraint;
import com.tenant.api.validation.PasswordConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class RegisterUserForm {
    @EmailConstraint
    @ApiModelProperty(required = true)
    private String email;

    @PasswordConstraint
    @ApiModelProperty(required = true)
    private String password;

    @NotBlank
    @ApiModelProperty(required = true)
    private String fullName;

}
