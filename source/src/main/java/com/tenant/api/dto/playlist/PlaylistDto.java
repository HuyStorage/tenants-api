package com.tenant.api.dto.playlist;

import com.tenant.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class PlaylistDto extends ABasicAdminDto {
    private String name;
    private Integer totalMovie;
}
