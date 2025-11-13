package com.tenant.api.dto.movieItem;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.dto.video.VideoLibraryDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ApiModel
public class MovieItemDto extends ABasicAdminDto {
    private String title;
    private String description;
    private Integer kind;
    private String label;
    private Integer ordering;
    private MovieItemDto parent;
    private MovieDto movie;
    private VideoLibraryDto video;
    private Date releaseDate;

    private List<MovieItemDto> episodes;
    private MovieItemDto trailer;
}
