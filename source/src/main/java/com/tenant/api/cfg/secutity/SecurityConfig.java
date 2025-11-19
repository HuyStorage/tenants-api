package com.tenant.api.cfg.secutity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/**",
            "/swagger-ui.html",
            "/webjars/**",

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
            "/v1/movie-item/get/**",
            "/v1/movie-item/list",
            "/v1/movie-person/list",
            "/v1/person/get/**",
            "/v1/person/list",
            "/v1/person/auto-complete",
            "/v1/sidebar/get/**",
            "/v1/sidebar/list",
            "/v1/comment/list",
            "/v1/comment/update",
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS);
        web.ignoring().antMatchers(PUBLIC_ENDPOINTS);

    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
