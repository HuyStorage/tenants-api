package com.tenant.api.cfg.component;


import com.tenant.api.constant.SecurityConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.service.LoggingService;
import com.tenant.api.service.impl.UserServiceImpl;
import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.jwt.TenantJwt;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    LoggingService loggingService;

    @Autowired
    private UserServiceImpl userService;

    final static List<Integer> BYPASS_TENANT_INFO = List.of(
            SecurityConstant.USER_KIND_ADMIN,
            SecurityConstant.USER_KIND_USER
    );
    final static List<String> BYPASS_JWT = List.of(
            "/v1/employee/login",
            "/v1/user/register",
            "/v1/user/login",
            "/v1/user/auth/social-login",
            "/v1/user/auth/web-callback",
            "/v1/user/auth/mobile-callback",
            "/v1/category/get/**",
            "/v1/category/list",
            "/v1/movie/get/slug/**",
            "/v1/movie/list",
            "/v1/movie-item/get/**",
            "/v1/movie-item/list",
            "/v1/movie-person/list",
            "/v1/person/get/**",
            "/v1/person/list",
            "/v1/person/auto-complete",
            "/v1/sidebar/get/**",
            "/v1/sidebar/list"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws IOException {
        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
                && request.getMethod().equals(HttpMethod.GET.name())) {
            loggingService.logRequest(request, null);
        }

        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        log.error("Starting call url: [" + getUrl(request) + "]");

        String tenantName = request.getHeader("X-tenant");
        if (isAllowed(request, BYPASS_JWT) && tenantName != null) {
            TenantDBContext.setCurrentTenant(tenantName);
            return true;
        }

        TenantJwt jwt = userService.getAddInfoFromToken();
        log.error("Token: {}", jwt);

        // super admin
        if (jwt != null && jwt.getIsSuperAdmin()) {
            TenantDBContext.setCurrentTenant(tenantName);
            return true;
        }
        // manager
        if (jwt != null && jwt.getTenantId() != null) {
            TenantDBContext.setCurrentTenant(jwt.getTenantId().split("&")[0]);
            return true;
        } else if (tenantName != null) {
            // employee
            if (jwt != null) {
                List<String> tenantContextList = Arrays.asList(jwt.getTenantId().split(":"));
                for (String tenantContext : tenantContextList) {
                    if (tenantContext.split("&")[0].equals(tenantName)) {
                        TenantDBContext.setCurrentTenant(tenantName);
                        return true;
                    }
                }
            } else {
                TenantDBContext.setCurrentTenant(tenantName);
                return true;
            }
        }
        // tenant error
        throw new UnauthorizationException("Invalid tenant: " + TenantDBContext.getCurrentTenant());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);

        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        log.error("Complete [" + getUrl(request) + "] executeTime : " + executeTime + "ms");

        if (ex != null) {
            log.error("afterCompletion>> " + ex.getMessage());

        }
    }

    /**
     * get full url request
     *
     * @param req
     * @return
     */
    private static String getUrl(HttpServletRequest req) {
        String reqUrl = req.getRequestURL().toString();
        String queryString = req.getQueryString();   // d=789
        if (!StringUtils.isEmpty(queryString)) {
            reqUrl += "?" + queryString;
        }
        return reqUrl;
    }

    private boolean handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setMessage(message);
        apiMessageDto.setResult(false);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getOutputStream().write(mapper.writeValueAsBytes(apiMessageDto));
        response.flushBuffer();
        return false;
    }

    private boolean isAllowed(HttpServletRequest request, List<String> whiteList) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return whiteList.stream().anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
    }
}
