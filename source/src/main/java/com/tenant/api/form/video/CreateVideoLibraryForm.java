package com.tenant.api.form.video;

import com.tenant.api.validation.SourceTypeConstraint;
import com.tenant.api.validation.StatusConstraint;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class CreateVideoLibraryForm {
    @NotBlank(message = "name cannot be empty")
    @ApiModelProperty(required = true)
    private String name;

    private String shortDescription;

    @NotBlank(message = "description cannot be empty")
    @ApiModelProperty(required = true)
    private String description;

    @SourceTypeConstraint
    @ApiModelProperty(required = true)
    private Integer sourceType;

    @NotBlank(message = "content cannot be empty")
    @ApiModelProperty(required = true)
    private String content;

    private String thumbnailUrl;

    private Long introStart;

    private Long introEnd;

    private Long outroStart;

    @StatusConstraint
    @ApiModelProperty(required = true)
    private Integer status;
}
