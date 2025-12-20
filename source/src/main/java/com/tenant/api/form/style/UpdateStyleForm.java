package com.tenant.api.form.style;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class UpdateStyleForm {
    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

    @NotNull(message = "type cannot be null")
    @ApiModelProperty(required = true)
    private Integer type;

    @NotBlank(message = "name cannot be empty")
    @ApiModelProperty(required = true)
    private String name;

    private String description;

    @NotBlank(message = "imageUrl cannot be null")
    @ApiModelProperty(required = true)
    private String imageUrl;

    @NotNull(message = "isDefault cannot be null")
    @ApiModelProperty(required = true)
    private Boolean isDefault;
}