package com.tenant.api.form.movie;

import com.tenant.api.validation.AgeRatingConstraint;
import com.tenant.api.validation.MovieTypeConstraint;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.List;

@Getter
@Setter
@ApiModel
public class FilterMovieForm {
    @MovieTypeConstraint(allowNull = true)
    private Integer type;

    @AgeRatingConstraint(allowNull = true)
    private Integer ageRating;

    private String language;

    private String country;

    private Boolean isFeatured;

    private List<Long> categoryIds;

    @Min(value = 1)
    private Integer limit;
}
