package com.tenant.api.service.feign;

import com.tenant.api.cfg.CustomFeignConfig;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.account.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-svr", url = "${auth.internal.base.url}", configuration = CustomFeignConfig.class)
public interface FeignCustomerAuthService {
    @GetMapping(value = "/v1/customer/get/{id}")
    ApiMessageDto<CustomerDto> get(@PathVariable("id") Long id);
}
