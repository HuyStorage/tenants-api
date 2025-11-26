package com.tenant.api.form.collection;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.form.movie.FilterMovieForm;
import com.tenant.api.validation.CollectionTypeConstraint;
import com.tenant.api.validation.ValidJsonField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ApiModel
public class UpdateCollectionForm {
    @NotNull(message = "id cannot be empty")
    @ApiModelProperty(required = true)
    private Long id;

    @NotBlank(message = "name cannot be empty")
    @ApiModelProperty(required = true)
    private String name;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "mainColor must be in hex format")
    @ApiModelProperty(required = true)
    private String color;

    @NotNull(message = "styleId cannot be null")
    @ApiModelProperty(required = true)
    private Long styleId;

    @CollectionTypeConstraint
    @ApiModelProperty(required = true)
    private Integer type;

    @NotNull(message = "randomData cannot be null")
    @ApiModelProperty(required = true)
    private Boolean randomData;

    @ApiModelProperty(required = true, example = BaseConstant.FILTER_MOVIE_SAMPLE_DATA)
    @ValidJsonField(classType = FilterMovieForm.class, allowNull = true)
    private String filter;
}