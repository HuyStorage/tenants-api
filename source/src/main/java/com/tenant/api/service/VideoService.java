package com.tenant.api.service;

import com.tenant.api.form.video.UpdateVideoForm;
import com.tenant.api.mapper.VideoLibraryMapper;
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

    @Autowired
    private VideoLibraryMapper videoLibraryMapper;

    public void updateVideoLibrary(UpdateVideoForm form) {
        log.warn("Start updating video ID: {}", form.getId());
        log.warn(form.getContent());
        VideoLibrary videoLibrary = videoLibraryRepository.findById(form.getId()).orElse(null);
        if (videoLibrary != null) {
            videoLibraryMapper.fromUpdateVideoFormToEntity(form, videoLibrary);
            videoLibraryRepository.save(videoLibrary);
        }
        log.warn("End updating video ID: {}", form.getId());
    }
}
