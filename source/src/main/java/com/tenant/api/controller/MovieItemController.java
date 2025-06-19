package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.movieItem.MovieItemDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.movieItem.CreateMovieItemForm;
import com.tenant.api.form.movieItem.OrderingMovieItemForm;
import com.tenant.api.form.movieItem.UpdateMovieItemForm;
import com.tenant.api.mapper.MovieItemMapper;
import com.tenant.api.storage.tenant.criteria.MovieItemCriteria;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.model.MovieItem;
import com.tenant.api.storage.tenant.model.VideoLibrary;
import com.tenant.api.storage.tenant.repository.MovieItemRepository;
import com.tenant.api.storage.tenant.repository.MovieRepository;
import com.tenant.api.storage.tenant.repository.VideoLibraryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            isRequiredVideo = true;
        }

        if (isRequiredVideo) {
            if (form.getVideoId() == null) {
                throw new BadRequestException("[Movie Item] Video is required", ErrorCode.MOVIE_ITEM_ERROR_VIDEO_REQUIRED);
            }
            video = videoLibraryRepository.findById(form.getVideoId())
                    .orElseThrow(() -> new NotFoundException("[Video Library] Video not found", ErrorCode.VIDEO_LIBRARY_ERROR_NOT_FOUND));
        }

        Integer ordering = (parent != null)
                ? movieItemRepository.findMaxOrdering(parent.getId(), form.getMovieId())
                : movieItemRepository.findMaxOrderingForSeason(form.getMovieId());

        MovieItem movieItem = movieItemMapper.fromCreateMovieItemFormToEntity(form);
        movieItem.setParent(parent);
        movieItem.setVideo(video);
        movieItem.setMovie(movie);
        movieItem.setOrdering((ordering != null) ? ordering + 1 : 0);

        movieItemRepository.save(movieItem);

        return makeSuccessResponse("Create movie item success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_V')")
    public ApiMessageDto<MovieItemDto> getById(@PathVariable("id") Long id) {
        MovieItem movieItem = movieItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Movie Item] Not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));

        return makeSuccessResponse(movieItemMapper.entityToMovieItemDto(movieItem), "Get movie item success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_L')")
    public ApiMessageDto<ResponseListDto<List<MovieItemDto>>> list(MovieItemCriteria criteria, Pageable pageable) {
        Page<MovieItem> movieItems = movieItemRepository.findAll(criteria.getSpecification(), pageable);

        ResponseListDto<List<MovieItemDto>> responseListDto = makeResponseListDto(movieItems, movieItemMapper::fromEntityToMovieItemAutoCompleteDtoList);
        return makeSuccessResponse(responseListDto, "List movie item success");
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

        if (isRequiredVideo) {
            if (form.getVideoId() == null) {
                throw new BadRequestException("[Movie Item] Video is required", ErrorCode.MOVIE_ITEM_ERROR_VIDEO_REQUIRED);
            }
            video = videoLibraryRepository.findById(form.getVideoId())
                    .orElseThrow(() -> new NotFoundException("[Video Library] Video not found", ErrorCode.VIDEO_LIBRARY_ERROR_NOT_FOUND));
        }
        movieItemMapper.fromUpdateMovieItemFormToEntity(form, movieItem);
        movieItem.setVideo(video);
        movieItemRepository.save(movieItem);
        return makeSuccessResponse("Update movie item success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        MovieItem movieItem = movieItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Movie Item] Not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));
        movieItemRepository.deleteByParentId(movieItem.getId());
        movieItemRepository.delete(movieItem);
        return makeSuccessResponse("Delete movie item success");
    }

    @PutMapping(value = "/update-ordering", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_I_U')")
    public ApiMessageDto<Void> updateOrdering(@RequestBody List<@Valid OrderingMovieItemForm> form) {
        if (form == null || form.isEmpty()) {
            throw new BadRequestException("Input list cannot be empty", ErrorCode.MOVIE_ITEM_ERROR_INVALID_REQUEST);
        }
        List<Long> ids = form.stream()
                .map(OrderingMovieItemForm::getId)
                .collect(Collectors.toList());
        List<MovieItem> movieItems = movieItemRepository.findAllById(ids);

        if (movieItems.size() != ids.size()) {
            throw new NotFoundException("[Movie Item] Not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND);
        }

        Map<Long, Integer> orderingMap = form.stream()
                .collect(Collectors.toMap(OrderingMovieItemForm::getId, OrderingMovieItemForm::getOrdering));

        for (MovieItem item : movieItems) {
            item.setOrdering(orderingMap.get(item.getId()));
        }
        movieItemRepository.saveAll(movieItems);

        return makeSuccessResponse("Update movie item success");
    }
}
