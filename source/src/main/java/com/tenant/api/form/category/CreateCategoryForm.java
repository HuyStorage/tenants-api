package com.tenant.api.form.category;

import com.tenant.api.validation.StatusConstraint;
import com.tenant.api.validation.UsernameConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class CreateCategoryForm {
//    @UsernameConstraint
    @NotBlank(message = "name cannot be empty")
    @ApiModelProperty(required = true)
    private String name;

    @StatusConstraint
    @ApiModelProperty(required = true)
    private Integer status;
}
