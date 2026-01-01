package com.tenant.api.cfg.secutity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Value("${auth.signing.key}")
    private String signingKey;
    @Autowired
    private CustomTokenConverter customTokenConverter;

    private static final String[] PUBLIC_ENDPOINTS = {
            // LOGIN
            "/v1/employee/login",
            "/v1/user/login",
            "/v1/user/register",
            "/v1/user/verify-otp",
            "/v1/user/resend-otp",
            "/v1/user/request-forgot-password",
            "/v1/user/forgot-password",
            "/v1/user/auth/social-login",
            "/v1/user/auth/web-callback",
            "/v1/user/auth/mobile-callback",

            // Public GET APIs
            "/v1/category/get/**",
            "/v1/category/list",
            "/v1/movie/get/**",
            "/v1/movie/list",
            "/v1/movie/suggestion/**",
            "/v1/movie-item/get/**",
            "/v1/movie-item/list",
            "/v1/movie-person/list",
            "/v1/person/get/**",
            "/v1/person/list",
            "/v1/person/auto-complete",
            "/v1/sidebar/get/**",
            "/v1/sidebar/list",
            "/v1/comment/list",
            "/v1/review/list",
            "/v1/review/get",
            "/v1/app-version/check-version/**",
            "/v1/collection/list",
            "/v1/collection/topics",
            "/v1/collection-item/list"
    };

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(customTokenConverter);
        converter.setSigningKey(signingKey);
        return converter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/v2/api-docs", "/api-docs/**", "/index", "/actuator/**", "/pub/**").permitAll()
                .antMatchers(PUBLIC_ENDPOINTS).permitAll()
                .antMatchers("/**").authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("movie-hub-service");
    }
}
