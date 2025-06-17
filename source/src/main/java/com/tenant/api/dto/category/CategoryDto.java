package com.tenant.api.dto.category;

import com.tenant.api.dto.ABasicAdminDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class CategoryDto extends ABasicAdminDto {
    private String name;
    private String slug;
}
