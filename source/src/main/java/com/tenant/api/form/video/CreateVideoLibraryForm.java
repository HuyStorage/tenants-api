package com.tenant.api.form.video;

import com.tenant.api.validation.StatusConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class CreateVideoLibraryForm {

    @NotBlank(message = "name cannot be empty")
    @ApiModelProperty(required = true)
    private String name;

    @NotBlank(message = "originalUrl cannot be empty")
    @ApiModelProperty(required = true)
    private String originalUrl;

    @NotBlank(message = "hlsUrl cannot be empty")
    @ApiModelProperty(required = true)
    private String hlsUrl;

    @StatusConstraint
    @ApiModelProperty(required = true)
    private Integer status;
}
