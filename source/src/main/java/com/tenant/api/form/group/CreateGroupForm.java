package com.tenant.api.form.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ApiModel
public class CreateGroupForm {
    @NotEmpty(message = "name cant not be null")
    @ApiModelProperty(required = true)
    private String name;

    @NotEmpty(message = "description cant not be null")
    @ApiModelProperty(required = true)
    private String description;

    @NotNull(message = "permissions cant not be null")
    @ApiModelProperty(required = true)
    private List<Long> permissions;

    @ApiModelProperty(name = "kind")
    private Integer kind;
}
