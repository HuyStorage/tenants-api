package com.tenant.api.service.feign;

import com.tenant.api.cfg.CustomFeignConfig;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.groupPermission.GroupPermissionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "permission-svr", url = "${auth.internal.base.url}", configuration = CustomFeignConfig.class)
public interface FeignPermissionAuthService {
    @GetMapping(value = "/v1/permission/list-by-ids")
    ApiMessageDto<List<GroupPermissionDto>> getPermissionByIds(@RequestParam("ids") List<Long> ids);
}
