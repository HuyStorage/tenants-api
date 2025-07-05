package com.tenant.api.controller;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.category.CategoryDto;
import com.tenant.api.dto.video.VideoLibraryDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.category.CreateCategoryForm;
import com.tenant.api.form.category.UpdateCategoryForm;
import com.tenant.api.form.video.CreateVideoLibraryForm;
import com.tenant.api.form.video.UpdateVideoLibraryForm;
import com.tenant.api.mapper.CategoryMapper;
import com.tenant.api.mapper.VideoLibraryMapper;
import com.tenant.api.service.rabbit.RabbitService;
import com.tenant.api.storage.tenant.criteria.CategoryCriteria;
import com.tenant.api.storage.tenant.criteria.VideoLibraryCriteria;
import com.tenant.api.storage.tenant.model.Category;
import com.tenant.api.storage.tenant.model.VideoLibrary;
import com.tenant.api.storage.tenant.repository.CategoryRepository;
import com.tenant.api.storage.tenant.repository.MovieItemRepository;
import com.tenant.api.storage.tenant.repository.VideoLibraryRepository;
import com.tenant.api.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/video-library")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class VideoLibraryController extends ABasicController {
    @Autowired
    private VideoLibraryRepository videoLibraryRepository;

    @Autowired
    private VideoLibraryMapper videoLibraryMapper;

    @Autowired
    private MovieItemRepository movieItemRepository;

    @Value("${rabbitmq.app}")
    private String appName;

    @Value("${rabbitmq.convert.video.queue}")
    private String convertVideoQueue;

    @Autowired
    private RabbitService rabbitService;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('VID_L_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreateVideoLibraryForm form) {
        VideoLibrary videoLibrary = videoLibraryRepository.findFirstByName(form.getName()).orElse(null);
        if (videoLibrary != null) {
            throw new BadRequestException("[Video Library] Name existed", ErrorCode.VIDEO_LIBRARY_ERROR_NAME_EXISTED);
        }
        videoLibrary = videoLibraryMapper.fromCreateVideoLibraryFormToEntity(form);
        videoLibrary.setState(BaseConstant.VIDEO_LIBRARY_STATE_PROCESSING);
        videoLibraryRepository.save(videoLibrary);
        VideoLibraryDto data = new VideoLibraryDto();
        data.setId(videoLibrary.getId());
        data.setContent(videoLibrary.getContent());
        rabbitService.handleSendMsg(appName, convertVideoQueue, data, "CMD_CONVERT_VIDEO", null, null, null, TenantDBContext.getCurrentTenant());
        return makeSuccessResponse("Create videoLibrary success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('VID_L_V')")
    public ApiMessageDto<VideoLibraryDto> get(@PathVariable("id") Long id) {
        VideoLibrary videoLibrary = videoLibraryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Video Library] Not found", ErrorCode.VIDEO_LIBRARY_ERROR_NOT_FOUND));
        return makeSuccessResponse(videoLibraryMapper.entityToVideoLibraryDto(videoLibrary), "Get video library success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('VID_L_L')")
    public ApiMessageDto<ResponseListDto<List<VideoLibraryDto>>> list(VideoLibraryCriteria criteria, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());
        Page<VideoLibrary> videoLibraries = videoLibraryRepository.findAll(criteria.getSpecification(), pageable);
        List<VideoLibraryDto> videoLibraryDtoList = videoLibraryMapper.fromEntityToVideoLibraryDtoList(videoLibraries.getContent());

        ResponseListDto<List<VideoLibraryDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(videoLibraryDtoList);
        responseListObj.setTotalPages(videoLibraries.getTotalPages());
        responseListObj.setTotalElements(videoLibraries.getTotalElements());
        return makeSuccessResponse(responseListObj, "List video library success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('VID_L_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateVideoLibraryForm form) {
        VideoLibrary videoLibrary = videoLibraryRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Video Library] Not found", ErrorCode.VIDEO_LIBRARY_ERROR_NOT_FOUND));
        if (!Objects.equals(videoLibrary.getName(), form.getName()) && videoLibraryRepository.existsByName(form.getName())) {
            throw new BadRequestException("[Video Library] Name existed", ErrorCode.VIDEO_LIBRARY_ERROR_NAME_EXISTED);
        }
        videoLibraryMapper.fromUpdateVideoLibraryFormToEntity(form, videoLibrary);
        videoLibraryRepository.save(videoLibrary);
        return makeSuccessResponse("Update video library success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('VID_L_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        VideoLibrary videoLibrary = videoLibraryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Video Library] Not found", ErrorCode.VIDEO_LIBRARY_ERROR_NOT_FOUND));
        if (movieItemRepository.existsByVideoId(videoLibrary.getId())) {
            throw new BadRequestException("[Video Library] Movie item existed", ErrorCode.VIDEO_LIBRARY_ERROR_MOVIE_ITEM_EXISTED);
        }
        videoLibraryRepository.delete(videoLibrary);
        return makeSuccessResponse("Delete video library success");
    }
}
