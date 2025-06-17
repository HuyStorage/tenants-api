package com.tenant.api.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class LoginAuthDto {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("user_kind")
    private Integer userKind;

    @JsonProperty("tenant_info")
    private String tenantInfo;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("additional_info")
    private String additionalInfo;

    @JsonProperty("jti")
    private String jti;
}
