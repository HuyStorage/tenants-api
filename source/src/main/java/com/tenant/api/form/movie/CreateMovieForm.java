package com.tenant.api.form.movie;

import com.tenant.api.validation.AgeRatingConstraint;
import com.tenant.api.validation.MovieTypeConstraint;
import com.tenant.api.validation.StatusConstraint;
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
public class CreateMovieForm {

    @NotBlank(message = "title cannot be empty")
    @ApiModelProperty(required = true)
    private String title;

    @NotBlank(message = "slug cannot be empty")
    @ApiModelProperty(required = true)
    private String slug;

    @NotBlank(message = "originalTitle cannot be empty")
    @ApiModelProperty(required = true)
    private String originalTitle;

    @NotBlank(message = "description cannot be empty")
    @ApiModelProperty(required = true)
    private String description;

    @NotBlank(message = "thumbnailUrl cannot be empty")
    @ApiModelProperty(required = true)
    private String thumbnailUrl;

    @NotBlank(message = "posterUrl cannot be empty")
    @ApiModelProperty(required = true)
    private String posterUrl;

    @NotBlank(message = "releaseDate cannot be empty")
    @ApiModelProperty(required = true)
    private Date releaseDate;

    @MovieTypeConstraint
    @ApiModelProperty(required = true)
    private Integer type;

    @ApiModelProperty
    private Boolean isFeatured;

    @ApiModelProperty
    private String language;

    @ApiModelProperty
    private String country;

    @AgeRatingConstraint
    @ApiModelProperty(required = true)
    private Integer ageRating;

    @ApiModelProperty
    private List<Long> categoryIds;

    @StatusConstraint
    @ApiModelProperty(required = true)
    private Integer status;

}
