package com.tenant.api.dto.category;

import com.tenant.api.dto.ABasicAdminDto;
import lombok.Data;


@Data
public class CategoryDto extends ABasicAdminDto {
    private String name;
}
