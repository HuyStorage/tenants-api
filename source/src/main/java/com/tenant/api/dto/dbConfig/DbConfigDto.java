package com.tenant.api.dto.dbConfig;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tenant.api.dto.LongToStringIfWebSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class DbConfigDto {
    @JsonSerialize(using = LongToStringIfWebSerializer.class)
    private Long id;
    private String name;
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private boolean initialize;
    private Integer updateStatus;
    private Integer maxConnection;
}
