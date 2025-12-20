package com.tenant.api.scheduler;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.dbConfig.DbConfigDto;
import com.tenant.api.service.feign.FeignDbConfigAuthService;
import com.tenant.api.storage.tenant.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class UserScheduler {
    @Autowired
    private FeignDbConfigAuthService feignDbConfigAuthService;

    @Autowired
    private AccountRepository accountRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void deleteUserPendingBefore1Days() {
        ApiMessageDto<List<DbConfigDto>> tenant = feignDbConfigAuthService.authGetList();
        if (tenant != null && tenant.getResult() && tenant.getData() != null) {
            for (DbConfigDto dbConfigDto : tenant.getData()) {
                try {
                    TenantDBContext.setCurrentTenant(dbConfigDto.getName());
                    Date date = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
                    accountRepository.deleteUserPendingBeforeDate(date);
                } catch (Exception e) {
                    log.error("Error occurred with schedule task deleteUserPendingBefore1Days: " + e.getMessage());
                }
            }
        }
    }
}
