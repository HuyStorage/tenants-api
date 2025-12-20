package com.tenant.api.form.movie;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel
public class FilterMovieForm {
    private Integer type;
    private Integer ageRating;
    private String language;
    private String country;
    private Boolean isFeatured;
    private List<Long> categoryIds;
}
