package com.tenant.api.scheduler;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.dbConfig.DbConfigDto;
import com.tenant.api.service.feign.FeignDbConfigAuthService;
import com.tenant.api.storage.tenant.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MovieScheduler {
    @Autowired
    private FeignDbConfigAuthService feignDbConfigAuthService;

    @Autowired
    private MovieRepository movieRepository;

    @Scheduled(cron = "0 0 */2 * * *", zone = "UTC")
    public void updateViewCount() {
        log.warn("======> Start scheduler updateViewCount movie");
        ApiMessageDto<List<DbConfigDto>> tenant = feignDbConfigAuthService.authGetList();
        if (tenant != null && tenant.getResult() && tenant.getData() != null) {
            for (DbConfigDto dbConfigDto : tenant.getData()) {
                try {
                    TenantDBContext.setCurrentTenant(dbConfigDto.getName());
                    movieRepository.updateViewCount();
                } catch (Exception e) {
                    log.error("Error occurred with schedule task updateViewCount: {}", e.getMessage());
                } finally {
                    TenantDBContext.clear();
                }
            }
        }
        log.warn("======> End scheduler updateViewCount movie");
    }
}
