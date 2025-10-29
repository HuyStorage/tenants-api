package com.tenant.api.form.favourite;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class CreateFavouriteForm {
    @NotNull(message = "type cannot be null")
    @ApiModelProperty(required = true)
    private Integer type;

    @NotNull(message = "targetId cannot be null")
    @ApiModelProperty(required = true)
    private Long targetId;
}
