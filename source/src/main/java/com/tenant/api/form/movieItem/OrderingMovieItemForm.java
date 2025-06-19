package com.tenant.api.form.movieItem;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class OrderingMovieItemForm {

    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

    @NotNull(message = "ordering cannot be null")
    @ApiModelProperty(required = true)
    private Integer ordering;
}
