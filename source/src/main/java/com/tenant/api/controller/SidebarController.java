package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.sidebar.SidebarDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.UpdateOrderingForm;
import com.tenant.api.form.sidebar.CreateSidebarForm;
import com.tenant.api.form.sidebar.UpdateSidebarForm;
import com.tenant.api.mapper.SidebarMapper;
import com.tenant.api.storage.tenant.criteria.SidebarCriteria;
import com.tenant.api.storage.tenant.model.MovieItem;
import com.tenant.api.storage.tenant.model.Sidebar;
import com.tenant.api.storage.tenant.repository.MovieItemRepository;
import com.tenant.api.storage.tenant.repository.SidebarRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/sidebar")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class SidebarController extends ABasicController {

    @Autowired
    private SidebarRepository sidebarRepository;

    @Autowired
    private SidebarMapper sidebarMapper;

    @Autowired
    private MovieItemRepository movieItemRepository;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SDB_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreateSidebarForm form) {
        if (sidebarRepository.existsByMovieItemIdAndActive(form.getMovieItemId(), BaseConstant.SIDEBAR_ACTIVE_TRUE)) {
            throw new BadRequestException("[Sidebar] Movie already exists", ErrorCode.SIDEBAR_ERROR_MOVIE_EXISTED);
        }

        MovieItem movieItem = movieItemRepository.findById(form.getMovieItemId())
                .orElseThrow(() -> new NotFoundException("[Movie Item] not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));

        Sidebar sidebar = sidebarMapper.fromCreateSidebarFormToEntity(form);
        sidebar.setMovieItem(movieItem);
        int ordering = sidebarRepository.findMaxOrdering().map(o -> o + 1).orElse(0);
        sidebar.setOrdering(ordering);

        sidebarRepository.save(sidebar);
        return makeSuccessResponse("Create sidebar success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<SidebarDto> get(@PathVariable("id") Long id) {
        Sidebar sidebar = sidebarRepository.findByIdAndActive(id, BaseConstant.SIDEBAR_ACTIVE_TRUE)
                .orElseThrow(() -> new NotFoundException("[Sidebar] Not found", ErrorCode.SIDEBAR_ERROR_NOT_FOUND));

        return makeSuccessResponse(sidebarMapper.entityToSidebarDto(sidebar), "Get sidebar success");
    }

    @GetMapping(value = "/admin/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SDB_V')")
    public ApiMessageDto<SidebarDto> getForAdmin(@PathVariable("id") Long id) {
        Sidebar sidebar = sidebarRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Sidebar] Not found", ErrorCode.SIDEBAR_ERROR_NOT_FOUND));

        return makeSuccessResponse(sidebarMapper.entityToSidebarDto(sidebar), "Get sidebar success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<SidebarDto>>> list(SidebarCriteria criteria, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(new Sort.Order(Sort.Direction.ASC, "ordering")));
        criteria.setActive(BaseConstant.SIDEBAR_ACTIVE_TRUE);
        Page<Sidebar> sidebars = sidebarRepository.findAll(criteria.getSpecification(), pageable);

        return makeSuccessResponse(makeResponseListDto(sidebars, sidebarMapper::fromEntityToSidebarDtoList), "List sidebar success");
    }

    @GetMapping(value = "/admin/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SDB_L')")
    public ApiMessageDto<ResponseListDto<List<SidebarDto>>> listForAdmin(SidebarCriteria criteria, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(new Sort.Order(Sort.Direction.ASC, "ordering")));
        Page<Sidebar> sidebars = sidebarRepository.findAll(criteria.getSpecification(), pageable);

        return makeSuccessResponse(makeResponseListDto(sidebars, sidebarMapper::fromEntityToSidebarDtoList), "List sidebar success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SDB_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateSidebarForm form) {
        Sidebar sidebar = sidebarRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Sidebar] Not found", ErrorCode.SIDEBAR_ERROR_NOT_FOUND));

        if (!Objects.equals(sidebar.getMovieItem().getId(), form.getMovieItemId())) {
            if (sidebarRepository.existsByMovieItemIdAndActive(form.getMovieItemId(), BaseConstant.SIDEBAR_ACTIVE_TRUE)) {
                throw new BadRequestException("[Sidebar] Movie already exists", ErrorCode.SIDEBAR_ERROR_MOVIE_EXISTED);
            }
            MovieItem movieItem = movieItemRepository.findById(form.getMovieItemId())
                    .orElseThrow(() -> new NotFoundException("[Movie Item] not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));
            sidebar.setMovieItem(movieItem);
        }

        sidebarMapper.fromUpdateSidebarFormToEntity(form, sidebar);

        sidebarRepository.save(sidebar);
        return makeSuccessResponse("Update sidebar success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SDB_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        Sidebar sidebar = sidebarRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Sidebar] Not found", ErrorCode.SIDEBAR_ERROR_NOT_FOUND));
        sidebarRepository.delete(sidebar);
        return makeSuccessResponse("Delete sidebar success");
    }

    @PutMapping(value = "/update-ordering", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SDB_U')")
    public ApiMessageDto<Void> updateOrdering(@RequestBody List<@Valid UpdateOrderingForm> form) {
        if (form == null || form.isEmpty()) {
            throw new BadRequestException("Input list cannot be empty", ErrorCode.MOVIE_ITEM_ERROR_INVALID_REQUEST);
        }

        List<Long> ids = form.stream()
                .map(UpdateOrderingForm::getId)
                .collect(Collectors.toList());
        List<Sidebar> sidebars = sidebarRepository.findAllById(ids);

        if (sidebars.size() != ids.size()) {
            throw new NotFoundException("[Sidebar] Not found", ErrorCode.SIDEBAR_ERROR_NOT_FOUND);
        }

        Map<Long, Integer> orderingMap = form.stream()
                .collect(Collectors.toMap(UpdateOrderingForm::getId, UpdateOrderingForm::getOrdering));

        for (Sidebar item : sidebars) {
            item.setOrdering(orderingMap.get(item.getId()));
        }
        sidebarRepository.saveAll(sidebars);

        return makeSuccessResponse("Update ordering sidebar success");
    }
}
