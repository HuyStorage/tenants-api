package com.tenant.api.dto.sns;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class SnsConfigDto {
    private Long applicationId;
    private Long applicationChannelId;
    private String secretKey;
}
