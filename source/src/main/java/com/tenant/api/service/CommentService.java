package com.tenant.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenant.api.dto.comment.AuthorInfoDto;
import com.tenant.api.storage.tenant.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommentService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    public void updateInfo(AuthorInfoDto infoDto) throws JsonProcessingException {
        commentRepository.updateCommentInfoByAuthorOrReply(infoDto.getId(), objectMapper.writeValueAsString(infoDto));
    }
}
