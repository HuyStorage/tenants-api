package com.tenant.api.form.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ApiModel
public class UpdateGroupForm {
    @NotNull(message = "id cant not be null")
    @ApiModelProperty(required = true)
    private Long id;

    @NotNull(message = "name cant not be null")
    @ApiModelProperty(required = true)
    private String name;

    private String description;

    @NotNull(message = "permissions cant not be null")
    @ApiModelProperty(required = true)
    private List<Long> permissions;
}
