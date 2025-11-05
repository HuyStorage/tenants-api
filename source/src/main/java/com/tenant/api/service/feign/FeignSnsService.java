package com.tenant.api.service.feign;

import com.tenant.api.cfg.CustomFeignConfig;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.sns.ClientTokenDto;
import com.tenant.api.dto.sns.SnsConfigDto;
import com.tenant.api.form.sns.GetClientTokenForm;
import com.tenant.api.form.sns.SendSignalForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "sns-svr", url = "${sns.internal.base.url}", configuration = CustomFeignConfig.class)
public interface FeignSnsService {
    @PostMapping(value = "/v1/application-channel/get-client-token")
    ApiMessageDto<ClientTokenDto> getClientToken(@RequestBody GetClientTokenForm form);

    @PostMapping(value = "/v1/application-channel/send-signal")
    ApiMessageDto<Void> sendSignal(@RequestBody SendSignalForm form);

    @GetMapping(value = "/v1/application-channel/get-sns-config")
    ApiMessageDto<SnsConfigDto> getSnsConfig(@RequestParam("appId") Long appId, @RequestParam("name") String name);
}
