package com.tenant.api.dto.movieItem;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.dto.video.VideoLibraryDto;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.model.VideoLibrary;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class MovieItemDto extends ABasicAdminDto {
    private String title;
    private String description;
    private Integer kind;
    private Integer ordering;
    private MovieItemDto parent;
    private MovieDto movie;
    private VideoLibraryDto video;
}
