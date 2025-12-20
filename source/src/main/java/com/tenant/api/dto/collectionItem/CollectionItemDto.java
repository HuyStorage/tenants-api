package com.tenant.api.dto.collectionItem;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.movie.MovieDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class CollectionItemDto extends ABasicAdminDto {
    private Long collectionId;
    private MovieDto movie;
    private Integer ordering;
}
