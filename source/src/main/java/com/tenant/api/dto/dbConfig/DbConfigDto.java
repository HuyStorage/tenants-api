package com.tenant.api.dto.dbConfig;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class DbConfigDto {
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
