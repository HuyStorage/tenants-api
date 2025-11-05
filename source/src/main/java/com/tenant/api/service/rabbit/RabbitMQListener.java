package com.tenant.api.service.rabbit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.constant.BaseConstant;
import com.tenant.api.form.rabbit.BaseSendMsgForm;
import com.tenant.api.form.video.UpdateVideoForm;
import com.tenant.api.service.SnsService;
import com.tenant.api.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQListener {
    @Value("${rabbitmq.update.video.queue}")
    private String updateVideoQueue;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VideoService videoService;

    @Autowired
    private SnsService snsService;

    @RabbitListener(queues = "${rabbitmq.update.video.queue}")
    public void receiveMessage(String message) {
        try {
            BaseSendMsgForm<UpdateVideoForm> baseMessageForm = objectMapper.readValue(message, new TypeReference<>() {
            });
            System.out.println("======> Received message from " + updateVideoQueue + ": " + message);
            if (baseMessageForm.getCmd().equals(BaseConstant.CMD_DONE_CONVERT_VIDEO)) {
                log.warn("==> Processing update video");
                TenantDBContext.setCurrentTenant(baseMessageForm.getTenantId());
                videoService.updateVideoLibrary(baseMessageForm.getData());

                // send sns
                snsService.sendSignal(BaseConstant.APP_NAME_CMS, baseMessageForm.getCmd(), baseMessageForm.getData(), baseMessageForm.getTenantId());

                log.warn("==> DONE processing message");
            }
        } catch (Exception e) {
            log.error("Error processing received message: {}", e.getMessage(), e);
        }
    }
}
