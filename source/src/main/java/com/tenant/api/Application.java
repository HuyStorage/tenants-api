package com.tenant.api;

import com.tenant.api.service.feign.FeignAccountAuthService;
import com.tenant.api.service.feign.FeignConst;
import com.tenant.api.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
@Slf4j
//@ComponentScan(basePackages = {"com.elms.api"})
@EnableFeignClients
@EnableConfigurationProperties({LiquibaseProperties.class})
public class Application {

    @Autowired
    FeignAccountAuthService accountAuthService;

    @Autowired
    UserServiceImpl userService;

    @Value("${auth.internal.username}")
    private String username;
    @Value("${auth.internal.password}")
    private String password;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void initialize() {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("grant_type", "password");
        request.add("username", username);
        request.add("password", password);
        OAuth2AccessToken result = accountAuthService.authLogin(FeignConst.LOGIN_TYPE_INTERNAL, request);
        if (result == null || result.getValue() == null) {
            throw new RuntimeException("APPLICATION FAILED TO START: CAN NOT GET KEY ");
        }
        userService.AUTH_SERVER_TOKEN = result.getValue();
        userService.AUTH_SERVER_REFRESH_TOKEN = result.getRefreshToken().getValue();
        userService.AUTH_SERVER_TOKEN_EXPIRES = result.getExpiration();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
