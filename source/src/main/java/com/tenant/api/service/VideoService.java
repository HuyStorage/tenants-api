package com.tenant.api.service;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.form.video.UpdateVideoForm;
import com.tenant.api.storage.tenant.model.VideoLibrary;
import com.tenant.api.storage.tenant.repository.VideoLibraryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VideoService {
    @Autowired
    private VideoLibraryRepository videoLibraryRepository;

    public void updateVideoLibrary(UpdateVideoForm form) {
        VideoLibrary videoLibrary = videoLibraryRepository.findById(form.getId()).orElse(null);
        if (videoLibrary != null) {
            videoLibrary.setContent(form.getContent());
            videoLibrary.setState(form.getState());
            videoLibraryRepository.save(videoLibrary);
        }
    }
}
