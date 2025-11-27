package com.tenant.api.form.playlist;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class ActionUpdatePlaylistForm {
    @NotNull(message = "playlistId cannot be empty")
    @ApiModelProperty(required = true)
    private Long playlistId;

    @NotNull(message = "action cannot be empty")
    @ApiModelProperty(required = true)
    private Integer action;
}