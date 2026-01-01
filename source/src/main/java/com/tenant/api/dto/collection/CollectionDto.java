package com.tenant.api.dto.collection;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.collectionItem.CollectionItemDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.dto.style.StyleDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel
public class CollectionDto extends ABasicAdminDto {
    private String name;
    private String color;
    private Integer ordering;
    private Integer styleType;
    private StyleDto style;
    private Integer type;
    private String filter;
    private List<CollectionItemDto> collectionItems;
    private List<MovieDto> movies;
}
