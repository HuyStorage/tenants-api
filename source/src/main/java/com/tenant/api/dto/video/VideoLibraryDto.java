package com.tenant.api.dto.video;

import com.tenant.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class VideoLibraryDto extends ABasicAdminDto {
    private String name;
    private String originalUrl;
    private String hlsUrl;
}
