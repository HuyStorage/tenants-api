package com.tenant.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.sns.ClientTokenDto;
import com.tenant.api.dto.sns.SnsConfigDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.sns.BaseSendSignalForm;
import com.tenant.api.form.sns.GetClientTokenForm;
import com.tenant.api.form.sns.SendSignalForm;
import com.tenant.api.service.feign.FeignSnsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SnsService {
    @Value("${sns.application.id}")
    private Long applicationId;

    @Autowired
    private FeignSnsService feignSnsService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Save sns config of tenant
     * key channelName = {tenantId}_{appName}
     */
    public ConcurrentHashMap<String, SnsConfigDto> globalSnsConfigMaps = new ConcurrentHashMap<>();

    public ClientTokenDto getClientToken(String appName, String tenantId) {
        SnsConfigDto appConfig = getAppConfig(appName, tenantId);

        GetClientTokenForm getClientTokenForm = new GetClientTokenForm();
        getClientTokenForm.setApplicationId(appConfig.getApplicationId());
        getClientTokenForm.setApplicationChannelId(appConfig.getApplicationChannelId());
        getClientTokenForm.setSecretKey(appConfig.getSecretKey());

        ApiMessageDto<ClientTokenDto> result;
        try {
            result = feignSnsService.getClientToken(getClientTokenForm);
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.SNS_ERROR_GET_CLIENT_TOKEN, "SNS get client token failed: " + e.getMessage());
        }
        if (result == null || Boolean.FALSE.equals(result.getResult()) || result.getData() == null) {
            throw new BadRequestException(ErrorCode.SNS_ERROR_GET_CLIENT_TOKEN, "SNS get client token failed");
        }
        return result.getData();
    }

    public <T> void sendSignal(String appName, String cmd, T data, String tenantId) {
        SnsConfigDto appConfig = getAppConfig(appName, tenantId);

        BaseSendSignalForm<T> form = new BaseSendSignalForm<>();
        form.setCmd(cmd);
        form.setData(data);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(form);

            SendSignalForm sendSignalForm = new SendSignalForm();
            sendSignalForm.setApplicationId(appConfig.getApplicationId());
            sendSignalForm.setApplicationChannelId(appConfig.getApplicationChannelId());
            sendSignalForm.setSecretKey(appConfig.getSecretKey());
            sendSignalForm.setPayload(payload);

            feignSnsService.sendSignal(sendSignalForm);
        } catch (Exception e) {
            log.debug("=======> call sns error: /v1/application-channel/send-signal");
        }
    }

    public SnsConfigDto getSnsConfig(String tenantId, String appName) {
        SnsConfigDto snsConfig = feignSnsService.getSnsConfig(applicationId, getChannelName(appName, tenantId)).getData();
        if (snsConfig == null) {
            throw new NotFoundException("[SNS] SnsConfig not found", ErrorCode.SNS_ERROR_APP_CONFIG);
        }
        return snsConfig;
    }

    private SnsConfigDto getAppConfig(String appName, String tenantId) {
        if (tenantId == null) {
            throw new BadRequestException(ErrorCode.SNS_ERROR_APP_CONFIG, "[SNS] Missing tenant id");
        }
        if (globalSnsConfigMaps.containsKey(getChannelName(appName, tenantId))) {
            return globalSnsConfigMaps.get(getChannelName(appName, tenantId));
        }
        SnsConfigDto snsConfigDto = getSnsConfig(tenantId, appName);
        globalSnsConfigMaps.put(getChannelName(appName, tenantId), snsConfigDto);
        return snsConfigDto;
    }

    private String getChannelName(String appName, String tenantId) {
        return tenantId + "_" + appName;
    }
}
