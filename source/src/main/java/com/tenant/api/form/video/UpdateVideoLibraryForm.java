package com.tenant.api.form.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class UpdateVideoLibraryForm {
    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

    @NotBlank(message = "name cannot be empty")
    @ApiModelProperty(required = true)
    private String name;

    @NotBlank(message = "originalUrl cannot be empty")
    @ApiModelProperty(required = true)
    private String originalUrl;

    @NotBlank(message = "hlsUrl cannot be empty")
    @ApiModelProperty(required = true)
    private Integer hlsUrl;
}
