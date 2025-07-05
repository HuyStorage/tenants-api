package com.tenant.api.form.movieItem;

import com.tenant.api.validation.StatusConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class UpdateMovieItemForm {
    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

    @NotBlank(message = "title cannot be empty")
    @ApiModelProperty(required = true)
    private String title;

    @NotBlank(message = "description cannot be empty")
    @ApiModelProperty(required = true)
    private String description;

    @StatusConstraint
    @ApiModelProperty(required = true)
    private Integer status;

    private Long videoId;
}
