package com.tenant.api.dto.movie;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.category.CategoryDto;
import com.tenant.api.dto.movieItem.MovieItemDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel
public class MovieDto extends ABasicAdminDto {
    private String title;
    private String originalTitle;
    private String slug;
    private String description;
    private String thumbnailUrl;
    private String posterUrl;
    private Date releaseDate;
    private Integer type;
    private Boolean isFeatured;
    private String language;
    private String country;
    private Integer ageRating;
    private List<CategoryDto> categories;
    private Long viewCount;
    private List<MovieItemDto> seasons;
}
