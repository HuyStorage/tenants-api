package com.tenant.api.form.sns;

import com.tenant.api.validation.SnsAppName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GetSnsTokenForm {
    @SnsAppName
    @ApiModelProperty(name = "appName")
    private String appName;
}
