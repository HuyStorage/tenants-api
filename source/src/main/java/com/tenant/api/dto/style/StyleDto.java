package com.tenant.api.dto.style;

import com.tenant.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class StyleDto extends ABasicAdminDto {
    private Integer type;
    private String name;
    private String description;
    private String imageMobileUrl;
    private String imageWebUrl;
    private Boolean isDefault;
}
