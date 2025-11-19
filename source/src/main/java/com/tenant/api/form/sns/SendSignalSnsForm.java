package com.tenant.api.form.sns;

import com.tenant.api.validation.SnsAppName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class SendSignalSnsForm {
    @SnsAppName
    @ApiModelProperty(name = "appName", required = true)
    private String appName;

    @NotBlank
    @ApiModelProperty(name = "payload", required = true)
    private String payload;
}
