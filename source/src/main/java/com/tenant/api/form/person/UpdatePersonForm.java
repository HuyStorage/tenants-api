package com.tenant.api.form.person;

import com.tenant.api.validation.GenderConstraint;
import com.tenant.api.validation.PersonKindConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel
public class UpdatePersonForm {
    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

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
    @ApiModelProperty
    private Integer gender;

    @ApiModelProperty
    private Date dateOfBirth;

    private String country;

    @PersonKindConstraint
    @ApiModelProperty(required = true)
    private List<Integer> kinds;
}
