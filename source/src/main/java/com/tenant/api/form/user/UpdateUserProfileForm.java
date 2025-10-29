package com.tenant.api.form.user;

import com.tenant.api.validation.EmailConstraint;
import com.tenant.api.validation.GenderConstraint;
import com.tenant.api.validation.PhoneConstraint;
import com.tenant.api.validation.UsernameConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class UpdateUserProfileForm {
    @UsernameConstraint(allowNull = true)
    @ApiModelProperty(name = "username")
    private String username;

    @EmailConstraint
    @ApiModelProperty(name = "email", required = true)
    private String email;

    @PhoneConstraint(allowNull = true)
    @ApiModelProperty(name = "phone")
    private String phone;

    @NotBlank(message = "fullName cant not be empty")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;

    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;

    @GenderConstraint
    @ApiModelProperty(name = "gender")
    private Integer gender;
}
