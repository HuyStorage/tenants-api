package com.tenant.api.service;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.form.file.DeleteListFileForm;
import com.tenant.api.service.feign.FeignFileMediaService;
import com.tenant.api.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MediaService {
    @Autowired
    private FeignFileMediaService feignFileMediaService;

    @Autowired
    private UserServiceImpl userService;

    public void deleteFile(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            feignFileMediaService.deleteListFile(TenantDBContext.getCurrentTenant(), userService.getBearerTokenHeader(), new DeleteListFileForm(Collections.singletonList(filePath)));
        }
    }

    public void deleteFiles(DeleteListFileForm deleteListFileForm) {
        if (deleteListFileForm != null && !deleteListFileForm.getFiles().isEmpty()) {
            feignFileMediaService.deleteListFile(TenantDBContext.getCurrentTenant(), userService.getBearerTokenHeader(), deleteListFileForm);
        }
    }

    public void deleteFiles(List<String> filePaths) {
        if (filePaths != null && !filePaths.isEmpty()) {
            List<String> filesToDelete = new ArrayList<>();
            for (String filePath : filePaths) {
                if (StringUtils.isNotBlank(filePath)) {
                    filesToDelete.add(filePath);
                }
            }
            if (!filesToDelete.isEmpty()) {
                feignFileMediaService.deleteListFile(TenantDBContext.getCurrentTenant(), userService.getBearerTokenHeader(), new DeleteListFileForm(filesToDelete));
            }
        }
    }
}