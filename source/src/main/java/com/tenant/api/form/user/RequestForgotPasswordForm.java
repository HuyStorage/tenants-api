package com.tenant.api.form.user;

import com.tenant.api.validation.EmailConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class RequestForgotPasswordForm {
    @EmailConstraint
    @ApiModelProperty(required = true)
    private String email;
}
