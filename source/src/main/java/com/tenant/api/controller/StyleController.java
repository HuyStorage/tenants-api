package com.tenant.api.controller;

import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.style.StyleDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.style.CreateStyleForm;
import com.tenant.api.form.style.UpdateStyleForm;
import com.tenant.api.mapper.StyleMapper;
import com.tenant.api.service.MediaService;
import com.tenant.api.storage.tenant.criteria.StyleCriteria;
import com.tenant.api.storage.tenant.model.Style;
import com.tenant.api.storage.tenant.repository.CollectionRepository;
import com.tenant.api.storage.tenant.repository.StyleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/style")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class StyleController extends ABasicController {
    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private StyleMapper styleMapper;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private MediaService mediaService;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STL_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreateStyleForm form) {
        if (styleRepository.existsByType(form.getType())) {
            throw new BadRequestException("[Style] type existed", ErrorCode.STYLE_ERROR_TYPE_EXISTED);
        }

        if (form.getIsDefault()) {
            styleRepository.resetDefault();
        } else if (!styleRepository.existsByIdNotNull()) {
            form.setIsDefault(true);
        }

        Style style = styleMapper.fromCreateStyleFormToEntity(form);
        styleRepository.save(style);
        return makeSuccessResponse("Create style success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STL_V')")
    public ApiMessageDto<StyleDto> get(@PathVariable("id") Long id) {
        Style style = styleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Style] Not found", ErrorCode.STYLE_ERROR_NOT_FOUND));
        return makeSuccessResponse(styleMapper.entityToStyleDto(style), "Get style success.");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STL_L')")
    public ApiMessageDto<ResponseListDto<List<StyleDto>>> list(StyleCriteria criteria, Pageable pageable) {
        Page<Style> styles = styleRepository.findAll(criteria.getSpecification(), pageable);
        return makeSuccessResponse(makeResponseListDto(styles, styleMapper::entityToStyleDtoList), "List style success");
    }

    @GetMapping(value = "/auto-complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<StyleDto>>> autoComplete(StyleCriteria criteria, Pageable pageable) {
        Page<Style> styles = styleRepository.findAll(criteria.getSpecification(), pageable);
        return makeSuccessResponse(makeResponseListDto(styles, styleMapper::entityToStyleAutoCompleteDtoList), "List style success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STL_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateStyleForm form) {
        Style style = styleRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Style] Not found", ErrorCode.STYLE_ERROR_NOT_FOUND));

        if (style.getIsDefault() && !form.getIsDefault()) {
            throw new BadRequestException("[Style] not have default", ErrorCode.STYLE_ERROR_TYPE_NOT_HAVE_DEFAULT);
        }

        if (!Objects.equals(form.getImageUrl(), style.getImageUrl())) {
            mediaService.deleteFile(style.getImageUrl());
        }

        if (!style.getIsDefault() && form.getIsDefault()) {
            styleRepository.resetDefault();
        }

        styleMapper.fromUpdateStyleFormToEntity(form, style);
        styleRepository.save(style);
        return makeSuccessResponse("Update style success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STL_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        Style style = styleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Style] Not found", ErrorCode.STYLE_ERROR_NOT_FOUND));
        if (style.getIsDefault()) {
            throw new BadRequestException("[Style] not have default", ErrorCode.STYLE_ERROR_TYPE_NOT_HAVE_DEFAULT);
        }

        mediaService.deleteFile(style.getImageUrl());
        collectionRepository.updateStyleToDefault(style.getId());
        styleRepository.delete(style);
        return makeSuccessResponse("Delete style success.");
    }
}
