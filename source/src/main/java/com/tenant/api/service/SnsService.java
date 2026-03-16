package com.tenant.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenant.api.constant.BaseConstant;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.form.sns.BaseSendSignalForm;
import com.tenant.api.service.rabbit.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SnsService {
    @Value("${rabbitmq.app}")
    private String appName;

    @Value("${rabbitmq.sns.broadcast.queue}")
    private String queueName;

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private ObjectMapper objectMapper;

    public <T> void sendSignalForAllTenantApp(T data, String tenantId) {
        sendSignal(data, BaseConstant.APP_TENANT, tenantId);
    }

    public <T> void sendSignal(T data, String app, String tenantId) {
        try {
            BaseSendSignalForm<T> form = new BaseSendSignalForm<>();
            form.setPayload(data);
            form.setApp(app);

            rabbitService.handleSendMsg(
                    appName,
                    1 + "_" + queueName,
                    form,
                    BaseConstant.CMD_BROADCAST,
                    null,
                    null,
                    null,
                    tenantId
            );
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new BadRequestException("Failed to send signal: " + ex.getMessage());
        }
    }
}
