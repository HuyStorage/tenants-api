package com.tenant.api.dto.sidebar;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.dto.movieItem.MovieItemDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@ApiModel
public class SidebarDto extends ABasicAdminDto {
    private MovieItemDto movieItem;
    private String description;
    private String webThumbnailUrl;
    private String mobileThumbnailUrl;
    private String mainColor;
    private Integer ordering;
    private Boolean active;
}
