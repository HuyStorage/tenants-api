package com.tenant.api.form.reaction;

import com.tenant.api.validation.ReactionTypeConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class CreateReactionForm {
    @NotNull(message = "id cannot be null")
    @ApiModelProperty(required = true)
    private Long id;

    @ReactionTypeConstraint
    @ApiModelProperty(required = true)
    private Integer type;
}
