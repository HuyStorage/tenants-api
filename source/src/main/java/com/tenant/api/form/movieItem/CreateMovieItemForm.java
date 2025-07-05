package com.tenant.api.form.movieItem;

import com.tenant.api.validation.MovieItemKindConstraint;
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
public class CreateMovieItemForm {
    @NotBlank(message = "title cannot be empty")
    @ApiModelProperty(required = true)
    private String title;

    @NotBlank(message = "description cannot be empty")
    @ApiModelProperty(required = true)
    private String description;

    @MovieItemKindConstraint
    @ApiModelProperty(required = true)
    private Integer kind;

    private Long parentId; // can be null

    @NotNull(message = "movieId cannot be null")
    @ApiModelProperty(required = true)
    private Long movieId;

    private Long videoId; // can be null

    @NotNull(message = "ordering cannot be null")
    @ApiModelProperty(required = true)
    private Long ordering;

    @StatusConstraint
    @ApiModelProperty(required = true)
    private Integer status;
}
