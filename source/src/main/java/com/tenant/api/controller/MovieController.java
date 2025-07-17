package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.movie.CreateMovieForm;
import com.tenant.api.form.movie.UpdateMovieForm;
import com.tenant.api.mapper.MovieMapper;
import com.tenant.api.storage.tenant.criteria.MovieCriteria;
import com.tenant.api.storage.tenant.model.Category;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.repository.*;
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
@RequestMapping("/v1/movie")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class MovieController extends ABasicController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MovieItemRepository movieItemRepository;

    @Autowired
    private MoviePersonRepository moviePersonRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreateMovieForm form) {
        Movie movie = movieRepository.findFirstBySlugAndStatus(form.getSlug(), BaseConstant.STATUS_ACTIVE).orElse(null);
        if (movie != null) {
            throw new BadRequestException("[Movie] Slug existed", ErrorCode.MOVIE_ERROR_SLUG_EXISTED);
        }

        movie = movieMapper.fromCreateMovieFormToEntity(form);
        movie.getCategories().clear();
        if (form.getCategoryIds() != null && !form.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(form.getCategoryIds());
            movie.setCategories(categories);
        }
        movieRepository.save(movie);

        return makeSuccessResponse("Create movie success");
    }

    @GetMapping(value = "/get/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_V')")
    public ApiMessageDto<MovieDto> getById(@PathVariable("id") Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Movie] Not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));

        return makeSuccessResponse(movieMapper.entityToMovieDto(movie), "Get movie success");
    }

    @GetMapping(value = "/get/slug/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<MovieDto> getBySlug(@PathVariable("slug") String slug) {
        Movie movie = movieRepository.findFirstBySlugAndStatus(slug, BaseConstant.STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("[Movie] Not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));

        return makeSuccessResponse(movieMapper.entityToMovieDto(movie), "Get movie success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<MovieDto>>> list(MovieCriteria criteria, Pageable pageable) {
        criteria.setStatus(BaseConstant.STATUS_ACTIVE);
        Page<Movie> movies = movieRepository.findAll(criteria.getSpecification(), pageable);

        ResponseListDto<List<MovieDto>> responseListDto = makeResponseListDto(movies, movieMapper::fromEntityToMovieDtoList);
        return makeSuccessResponse(responseListDto, "List movie success");
    }

    @GetMapping(value = "/admin/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_L')")
    public ApiMessageDto<ResponseListDto<List<MovieDto>>> listForAdmin(MovieCriteria criteria, Pageable pageable) {
        Page<Movie> movies = movieRepository.findAll(criteria.getSpecification(), pageable);

        ResponseListDto<List<MovieDto>> responseListDto = makeResponseListDto(movies, movieMapper::fromEntityToMovieDtoList);
        return makeSuccessResponse(responseListDto, "List movie success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateMovieForm form) {
        Movie movie = movieRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Movie] Not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));
        if (!Objects.equals(movie.getSlug(), form.getSlug()) && movieRepository.existsBySlug(form.getSlug())) {
            throw new BadRequestException("[Movie] Slug existed", ErrorCode.MOVIE_ERROR_SLUG_EXISTED);
        }
        movieMapper.fromUpdateMovieFormToEntity(form, movie);

        if (form.getCategoryIds() != null && !form.getCategoryIds().isEmpty()) {
            movie.getCategories().clear();
            List<Category> categories = categoryRepository.findAllById(form.getCategoryIds());
            movie.setCategories(categories);
        }

        movieRepository.save(movie);
        return makeSuccessResponse("Update movie success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Movie] Not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));

        if (movieItemRepository.existsByMovieId(movie.getId())) {
            throw new BadRequestException("[Movie] Cannot delete, movie still has items", ErrorCode.MOVIE_ERROR_HAS_ITEM);
        }

        commentRepository.deleteByMovieId(movie.getId());

        // delete movie person
        moviePersonRepository.deleteByMovieId(movie.getId());

        // delete movie category
        movie.getCategories().clear();
        movieRepository.save(movie);

        movieRepository.delete(movie);
        return makeSuccessResponse("Delete movie success");
    }
}
