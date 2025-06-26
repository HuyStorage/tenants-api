package com.tenant.api.storage.audit;

import com.tenant.api.jwt.TenantJwt;
import com.tenant.api.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Optional<String> getCurrentAuditor() {
        TenantJwt tenantJwt = userService.getAddInfoFromToken();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || tenantJwt == null) {
            return Optional.of("unknown");
        }

        return Optional.of(tenantJwt.getAccountId().toString());
    }
}
