package com.tenant.api.form.collection;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.form.movie.FilterMovieForm;
import com.tenant.api.validation.CollectionTypeConstraint;
import com.tenant.api.validation.ColorConstraint;
import com.tenant.api.validation.ValidJsonField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ApiModel
public class CreateCollectionForm {
    @NotBlank(message = "name cannot be empty")
    @ApiModelProperty(required = true)
    private String name;

    @ApiModelProperty(value = "List of hex colors for gradient", example = "[\"#FF5733\", \"#FFC300\"]", required = true)
    @NotNull(message = "colors cannot be null")
    @Size(min = 2, message = "gradientColors must contain at least 2 colors")
    @Valid
    private List<@ColorConstraint String> colors;

    @NotNull(message = "ordering cannot be null")
    @ApiModelProperty(required = true)
    private Integer ordering;

    private Long styleId;

    @CollectionTypeConstraint
    @ApiModelProperty(required = true)
    private Integer type;

    @NotNull(message = "randomData cannot be null")
    @ApiModelProperty(required = true)
    private Boolean randomData;

    @ApiModelProperty(required = true, example = BaseConstant.FILTER_MOVIE_SAMPLE_DATA)
    @ValidJsonField(classType = FilterMovieForm.class)
    private String filter;
}