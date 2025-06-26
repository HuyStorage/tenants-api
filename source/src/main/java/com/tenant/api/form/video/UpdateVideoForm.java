package com.tenant.api.form.video;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UpdateVideoForm {
    private Long id;

    private String content;

    private Integer state;
}
