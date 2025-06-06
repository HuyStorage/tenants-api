package com.tenant.api.dto.account;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginAuthDto {
    @JsonProperty("access_token")
    private String accessToken;
}
