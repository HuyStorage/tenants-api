package com.tenant.api.form.person;

import com.tenant.api.validation.GenderConstraint;
import com.tenant.api.validation.PersonKindConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel
public class CreatePersonForm {
    @NotBlank
    @ApiModelProperty(required = true)
    private String name;

    @ApiModelProperty
    private String otherName;

    @ApiModelProperty
    private String avatarPath;

    @ApiModelProperty
    private String bio;

    @GenderConstraint
    @ApiModelProperty(required = true)
    private String gender;

    @ApiModelProperty
    private Date dateOfBirth;

    @PersonKindConstraint
    @ApiModelProperty(required = true)
    private List<Integer> kinds;
}
