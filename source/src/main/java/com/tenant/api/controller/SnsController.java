package com.tenant.api.controller;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.form.sns.BaseSendSignalPayloadForm;
import com.tenant.api.form.sns.SendSignalSnsForm;
import com.tenant.api.service.SnsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/sns")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class SnsController extends ABasicController {
    @Autowired
    private SnsService snsService;

    @PostMapping(value = "/send-signal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> sendSignal(@Valid @RequestBody SendSignalSnsForm form, BindingResult bindingResult) {
        BaseSendSignalPayloadForm<String> signalPayload = new BaseSendSignalPayloadForm<>();
        signalPayload.setCmd(form.getCmd());
        signalPayload.setSubCmd(form.getSubCmd());
        signalPayload.setData(form.getPayload());

        snsService.sendSignalForAllTenantApp(signalPayload, TenantDBContext.getCurrentTenant());
        return makeSuccessResponse("Send signal successfully");
    }
}
