package com.tenant.api.service.impl;

import com.tenant.api.jwt.TenantJwt;
import com.tenant.api.service.feign.FeignAccountAuthService;
import com.tenant.api.service.feign.FeignConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class UserServiceImpl {

    public String AUTH_SERVER_TOKEN = "";
    public String AUTH_SERVER_REFRESH_TOKEN = "";
    public Date AUTH_SERVER_TOKEN_EXPIRES = new Date();

    @Autowired
    private FeignAccountAuthService accountAuthService;

    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                return oauthDetails.getTokenValue();
            }
        }
        return null;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                Map<String, Object> map = (Map<String, Object>) oauthDetails.getDecodedDetails();
                String idStr = (String) map.get("user_id");
                if (idStr != null || !idStr.isEmpty()) {
                    userId = Long.parseLong(idStr);
                }
                return userId;
            }
        }
        return null;
    }

    public TenantJwt getAddInfoFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                Map<String, Object> map = (Map<String, Object>) oauthDetails.getDecodedDetails();
                String encodedData = (String) map.get("additional_info");
                //idStr -> json
                if (encodedData != null && !encodedData.isEmpty()) {
                    return TenantJwt.decode(encodedData);
                }
                return null;
            }
        }
        return null;
    }

    public String getCustomStringField(String key) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                Map<String, Object> map = (Map<String, Object>) oauthDetails.getDecodedDetails();
                return (String) map.get(key);
            }
        }
        return null;
    }

    public Long getCustomLongField(String key) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                Map<String, Object> map = (Map<String, Object>) oauthDetails.getDecodedDetails();
                return (Long) map.get(key);
            }
        }
        return null;
    }

    public String getToken() {
        long nowMillis = System.currentTimeMillis();
        long oneDayMillis = 24 * 60 * 60 * 1000L;

        // refresh token
        if (AUTH_SERVER_TOKEN_EXPIRES.getTime() <= nowMillis + oneDayMillis) {
            MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
            request.add("grant_type", "refresh_token");
            request.add("refresh_token", AUTH_SERVER_REFRESH_TOKEN);

            OAuth2AccessToken result = accountAuthService.authLogin(FeignConst.LOGIN_TYPE_INTERNAL, request);
            AUTH_SERVER_TOKEN = result.getValue();
            AUTH_SERVER_REFRESH_TOKEN = result.getRefreshToken().getValue();
            AUTH_SERVER_TOKEN_EXPIRES = result.getExpiration();
        }

        return AUTH_SERVER_TOKEN;
    }
}
