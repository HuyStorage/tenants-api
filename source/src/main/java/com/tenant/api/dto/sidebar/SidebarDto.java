package com.tenant.api.dto.sidebar;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.movie.MovieDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SidebarDto extends ABasicAdminDto {
    private MovieDto movie;
    private String description;
    private String webThumbnailUrl;
    private String mobileThumbnailUrl;
    private String mainColor;
    private Integer ordering;
    private Boolean active;
}
