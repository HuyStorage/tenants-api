package com.tenant.api.controller;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.movieItem.MovieItemDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.UpdateOrderingForm;
import com.tenant.api.form.movieItem.CreateMovieItemForm;
import com.tenant.api.form.movieItem.UpdateMovieItemForm;
import com.tenant.api.mapper.MovieItemMapper;
import com.tenant.api.service.MediaService;
import com.tenant.api.service.redis.RedisService;
import com.tenant.api.storage.tenant.criteria.MovieItemCriteria;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.model.MovieItem;
import com.tenant.api.storage.tenant.model.VideoLibrary;
import com.tenant.api.storage.tenant.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/movie-item")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class MovieItemController extends ABasicController {

    @Autowired
    private MovieItemRepository movieItemRepository;

    @Autowired
    private MovieItemMapper movieItemMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private VideoLibraryRepository videoLibraryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MediaService mediaService;
    @Autowired
    private WatchHistoryRepository watchHistoryRepository;

    @Transactional("tenantTransactionManager")
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreateMovieItemForm form) {
        Movie movie = movieRepository.findById(form.getMovieId())
                .orElseThrow(() -> new NotFoundException("[Movie] Movie not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));
        MovieItem parent = null;
        VideoLibrary video = null;

        boolean isRequiredVideo = false;
        if (Objects.equals(form.getKind(), BaseConstant.MOVIE_ITEM_KIND_SEASON)) {
            if (Objects.equals(movie.getType(), BaseConstant.MOVIE_TYPE_SINGLE)) {
                isRequiredVideo = true;
            }
        } else {
            if (form.getParentId() == null) {
                throw new BadRequestException("[Movie Item] Parent is required", ErrorCode.MOVIE_ITEM_ERROR_PARENT_REQUIRED);
            }
            parent = movieItemRepository.findById(form.getParentId())
                    .orElseThrow(() -> new NotFoundException("[Movie Item] Parent not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));
            if (form.getKind().equals(BaseConstant.MOVIE_ITEM_KIND_EPISODE) && Objects.equals(parent.getKind(), BaseConstant.MOVIE_ITEM_KIND_SEASON)) {
                movieItemRepository.increaseTotalEpisode(parent.getId());
            }
            isRequiredVideo = true;
        }

        if (isRequiredVideo && form.getVideoId() != null) {
//            if (form.getVideoId() == null) {
//                throw new BadRequestException("[Movie Item] Video is required", ErrorCode.MOVIE_ITEM_ERROR_VIDEO_REQUIRED);
//            }
            video = videoLibraryRepository.findById(form.getVideoId())
                    .orElseThrow(() -> new NotFoundException("[Video Library] Video not found", ErrorCode.VIDEO_LIBRARY_ERROR_NOT_FOUND));
        }

        MovieItem movieItem = movieItemMapper.fromCreateMovieItemFormToEntity(form);
        movieItem.setParent(parent);
        movieItem.setVideo(video);
        movieItem.setMovie(movie);

        movieItemRepository.save(movieItem);

        redisService.delete(redisService.buildKey(TenantDBContext.getCurrentTenant(), "movie", movie.getId().toString()));

        return makeSuccessResponse("Create movie item success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<MovieItemDto> get(@PathVariable("id") Long id) {
        MovieItem movieItem = movieItemRepository.findByIdAndStatus(id, BaseConstant.STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("[Movie Item] Not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));

        return makeSuccessResponse(movieItemMapper.entityToMovieItemDto(movieItem), "Get movie item success");
    }

    @GetMapping(value = "/admin/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_V')")
    public ApiMessageDto<MovieItemDto> getForAdmin(@PathVariable("id") Long id) {
        MovieItem movieItem = movieItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Movie Item] Not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));

        return makeSuccessResponse(movieItemMapper.entityToMovieItemDto(movieItem), "Get movie item success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<MovieItemDto>>> list(MovieItemCriteria criteria, Pageable pageable) {
        criteria.setStatus(BaseConstant.STATUS_ACTIVE);
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(new Sort.Order(Sort.Direction.ASC, "ordering")));
        Page<MovieItem> movieItems = movieItemRepository.findAll(criteria.getSpecification(), pageable);

        return makeSuccessResponse(makeResponseListDto(movieItems, movieItemMapper::fromEntityToMovieItemAutoCompleteDtoList), "List movie item success");
    }

    @GetMapping(value = "/admin/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_L')")
    public ApiMessageDto<ResponseListDto<List<MovieItemDto>>> listForAdmin(MovieItemCriteria criteria, Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(new Sort.Order(Sort.Direction.ASC, "ordering")));
        Page<MovieItem> movieItems = movieItemRepository.findAll(criteria.getSpecification(), pageable);

        return makeSuccessResponse(makeResponseListDto(movieItems, movieItemMapper::fromEntityToMovieItemAutoCompleteDtoList), "List movie item success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateMovieItemForm form) {
        MovieItem movieItem = movieItemRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Movie Item] Not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));

        boolean isRequiredVideo = false;
        VideoLibrary video = null;

        if (Objects.equals(movieItem.getKind(), BaseConstant.MOVIE_ITEM_KIND_SEASON)) {
            if (Objects.equals(movieItem.getMovie().getType(), BaseConstant.MOVIE_TYPE_SINGLE)) {
                isRequiredVideo = true;
            }
        } else {
            isRequiredVideo = true;
        }

        if (isRequiredVideo && form.getVideoId() != null) {
//            if (form.getVideoId() == null) {
//                throw new BadRequestException("[Movie Item] Video is required", ErrorCode.MOVIE_ITEM_ERROR_VIDEO_REQUIRED);
//            }
            video = videoLibraryRepository.findById(form.getVideoId())
                    .orElseThrow(() -> new NotFoundException("[Video Library] Video not found", ErrorCode.VIDEO_LIBRARY_ERROR_NOT_FOUND));
        }

        if (StringUtils.isNoneBlank(form.getThumbnailUrl())
                && StringUtils.isNoneBlank(movieItem.getThumbnailUrl())
                && !Objects.equals(form.getThumbnailUrl(), movieItem.getThumbnailUrl())) {
            mediaService.deleteFile(movieItem.getThumbnailUrl());
        }

        movieItemMapper.fromUpdateMovieItemFormToEntity(form, movieItem);
        movieItem.setVideo(video);
        movieItemRepository.save(movieItem);

        redisService.delete(redisService.buildKey(TenantDBContext.getCurrentTenant(), "movie", movieItem.getMovie().getId().toString()));

        return makeSuccessResponse("Update movie item success");
    }

    @Transactional("tenantTransactionManager")
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        MovieItem movieItem = movieItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Movie Item] Not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));

        if (movieItem.getThumbnailUrl() != null) {
            mediaService.deleteFile(movieItem.getThumbnailUrl());
        }

        if (Objects.equals(movieItem.getKind(), BaseConstant.MOVIE_ITEM_KIND_EPISODE)) {
            movieItemRepository.decreaseTotalEpisode(movieItem.getParent().getId());
        }

        List<Long> movieItemIds = new ArrayList<>();
        movieItemIds.add(id);
        if (Objects.equals(movieItem.getKind(), BaseConstant.MOVIE_ITEM_KIND_SEASON)) {
            movieItemIds.addAll(movieItemRepository.findAllByParentIdAndKindNot(id, BaseConstant.MOVIE_ITEM_KIND_TRAILER));
        }
        watchHistoryRepository.deleteByMovieItemIds(movieItemIds);

        commentRepository.deleteByMovieItemId(id);

        movieItemRepository.delete(movieItem);

        redisService.delete(redisService.buildKey(TenantDBContext.getCurrentTenant(), "movie", movieItem.getMovie().getId().toString()));

        return makeSuccessResponse("Delete movie item success");
    }

    @PutMapping(value = "/update-ordering", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_U')")
    public ApiMessageDto<Void> updateOrdering(@RequestBody List<@Valid UpdateOrderingForm> form) {
        if (form == null || form.isEmpty()) {
            throw new BadRequestException("Input list cannot be empty", ErrorCode.MOVIE_ITEM_ERROR_INVALID_REQUEST);
        }

        List<Long> ids = form.stream()
                .map(UpdateOrderingForm::getId)
                .collect(Collectors.toList());

        Map<Long, MovieItem> itemMap = movieItemRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(MovieItem::getId, Function.identity()));
        if (itemMap.size() != form.size()) {
            throw new NotFoundException("[Movie Item] Not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND);
        }
        for (UpdateOrderingForm f : form) {
            MovieItem movieItem = itemMap.get(f.getId());
            movieItem.setOrdering(f.getOrdering());
            movieItem.setParent(f.getParentId() != null ? itemMap.get(f.getParentId()) : null);
        }

        movieItemRepository.saveAll(itemMap.values());
        // sync data
        movieItemRepository.syncTotalEpisode();
        return makeSuccessResponse("Update movie item success");
    }
}
