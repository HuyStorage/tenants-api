package com.tenant.api.cfg.component;


import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.service.LoggingService;
import com.tenant.api.service.impl.UserServiceImpl;
import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.jwt.TenantJwt;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    LoggingService loggingService;

    @Autowired
    private UserServiceImpl userService;

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

        TenantJwt jwt = userService.getAddInfoFromToken();
        log.error("Token: {}", jwt);
        String tenantName = request.getHeader("X-tenant");
        if (jwt != null && jwt.getTenantId() != null) {
            TenantDBContext.setCurrentTenant(jwt.getTenantId().split("&")[0]);
            return true;
        } else if (tenantName != null) {
            TenantDBContext.setCurrentTenant(tenantName);
            return true;
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
}
