package com.tenant.api.controller;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.dto.movieItem.MovieItemDto;
import com.tenant.api.dto.watchHistory.WatchHistoryDto;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.movie.CreateMovieForm;
import com.tenant.api.form.movie.UpdateMovieForm;
import com.tenant.api.mapper.MovieItemMapper;
import com.tenant.api.mapper.MovieMapper;
import com.tenant.api.mapper.WatchHistoryMapper;
import com.tenant.api.service.MediaService;
import com.tenant.api.service.MovieService;
import com.tenant.api.service.redis.RedisService;
import com.tenant.api.storage.tenant.criteria.MovieCriteria;
import com.tenant.api.storage.tenant.criteria.MovieItemCriteria;
import com.tenant.api.storage.tenant.model.Collection;
import com.tenant.api.storage.tenant.model.*;
import com.tenant.api.storage.tenant.repository.*;
import com.tenant.api.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private MovieItemMapper movieItemMapper;

    @Autowired
    private MoviePersonRepository moviePersonRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WatchHistoryRepository watchHistoryRepository;

    @Autowired
    private WatchHistoryMapper watchHistoryMapper;

    @Autowired
    private CollectionItemRepository collectionItemRepository;

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private SidebarRepository sidebarRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistItemRepository playlistItemRepository;

    @Autowired
    private MovieService movieService;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreateMovieForm form) {
        Movie movie = movieMapper.fromCreateMovieFormToEntity(form);

        if (form.getCategoryIds() != null && !form.getCategoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(form.getCategoryIds());
            movie.setCategories(categories);
        }

        movie.setSlug(StringUtils.slugify(form.getTitle()));
        movieRepository.save(movie);
        return makeSuccessResponse("Create movie success");
    }

    @GetMapping(value = "/admin/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_V')")
    public ApiMessageDto<MovieDto> adminGet(@PathVariable("id") Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Movie] Not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));

        return makeSuccessResponse(movieMapper.entityToMovieDto(movie), "Get movie success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<MovieDto> get(@PathVariable("id") Long id) {
        // key -> {tenantId}::movie::{id}
        String key = redisService.buildKey(TenantDBContext.getCurrentTenant(), "movie", id.toString());
        MovieDto movieDto = redisService.get(key, MovieDto.class);
        if (movieDto != null) {
            redisService.refreshTTL(key, 5 * 60);
            return makeSuccessResponse(movieDto, "Get movie success");
        }

        Movie movie = movieRepository.findByIdAndStatus(id, BaseConstant.STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("[Movie] Not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));

        List<MovieItem> movieItems = movieItemRepository
                .findByMovieIdAndStatusWithParent(movie.getId(), BaseConstant.STATUS_ACTIVE);

        movieDto = movieMapper.entityToMovieDto(movie);
        movieDto.setSeasons(buildSeasonsWithEpisodes(movieItems));

        redisService.put(key, movieDto, 5 * 60);

        return makeSuccessResponse(movieDto, "Get movie success");
    }

    private List<MovieItemDto> buildSeasonsWithEpisodes(List<MovieItem> movieItems) {
        // Group items by kind
        Map<Integer, List<MovieItem>> itemsByKind = movieItems.stream()
                .collect(Collectors.groupingBy(MovieItem::getKind));

        // Create seasons map
        Map<Long, MovieItemDto> seasonMap = itemsByKind
                .getOrDefault(BaseConstant.MOVIE_ITEM_KIND_SEASON, Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(MovieItem::getOrdering))
                .map(item -> {
                    MovieItemDto dto = movieItemMapper.entityToMovieItemPublicDto(item);
                    dto.setEpisodes(new ArrayList<>());
                    return dto;
                })
                .collect(Collectors.toMap(
                        MovieItemDto::getId,
                        Function.identity(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        // Add episodes
        itemsByKind.getOrDefault(BaseConstant.MOVIE_ITEM_KIND_EPISODE, Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(MovieItem::getOrdering))
                .forEach(item -> {
                    if (item.getParent() != null) {
                        MovieItemDto season = seasonMap.get(item.getParent().getId());
                        if (season != null) {
                            season.getEpisodes().add(
                                    movieItemMapper.entityToMovieItemPublicDto(item)
                            );
                        }
                    }
                });

        // Add trailers
        Map<Long, MovieItem> maxTrailerBySeason = itemsByKind
                .getOrDefault(BaseConstant.MOVIE_ITEM_KIND_TRAILER, Collections.emptyList())
                .stream()
                .filter(item -> item.getParent() != null && seasonMap.containsKey(item.getParent().getId()))
                .collect(Collectors.toMap(
                        item -> item.getParent().getId(),                   // key = seasonId
                        Function.identity(),                                // value = trailer
                        (t1, t2) -> t1.getOrdering() > t2.getOrdering() ? t1 : t2
                ));

        maxTrailerBySeason.forEach((seasonId, trailer) -> {
            MovieItemDto season = seasonMap.get(seasonId);
            season.setTrailer(movieItemMapper.entityToMovieItemPublicDto(trailer));
        });

        return new ArrayList<>(seasonMap.values());
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<MovieDto>>> list(MovieCriteria criteria, Pageable pageable) {
        criteria.setStatus(BaseConstant.STATUS_ACTIVE);
        Page<Movie> movies = movieRepository.findAll(criteria.getSpecification(), pageable);

        return makeSuccessResponse(makeResponseListDto(movies, movieMapper::fromEntityToMovieDtoList), "List movie success");
    }

    @GetMapping(value = "/admin/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_L')")
    public ApiMessageDto<ResponseListDto<List<MovieDto>>> listForAdmin(MovieCriteria criteria, Pageable pageable) {
        Page<Movie> movies = movieRepository.findAll(criteria.getSpecification(), pageable);

        return makeSuccessResponse(makeResponseListDto(movies, movieMapper::fromEntityToMovieDtoList), "List movie success");
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateMovieForm form) {
        Movie movie = movieRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Movie] Not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));

        if (!Objects.equals(movie.getTitle(), form.getTitle())) {
            movie.setSlug(StringUtils.slugify(form.getTitle()));
        }

        if (form.getCategoryIds() != null && !form.getCategoryIds().isEmpty()) {
            movie.getCategories().clear();
            List<Category> categories = categoryRepository.findAllById(form.getCategoryIds());
            movie.setCategories(categories);
        }

        List<String> deletedFiles = new ArrayList<>();
        if (!Objects.equals(form.getThumbnailUrl(), movie.getThumbnailUrl())) {
            deletedFiles.add(movie.getThumbnailUrl());
        }
        if (!Objects.equals(form.getPosterUrl(), movie.getPosterUrl())) {
            deletedFiles.add(movie.getPosterUrl());
        }
        mediaService.deleteFiles(deletedFiles);

        movieMapper.fromUpdateMovieFormToEntity(form, movie);
        movieRepository.save(movie);

        log.debug("========> start remove movieId {}", movie.getId());
        redisService.delete(redisService.buildKey(TenantDBContext.getCurrentTenant(), "movie", movie.getId().toString()));
        return makeSuccessResponse("Update movie success");
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Movie] Not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));

        List<String> deletedFiles = new ArrayList<>();
        deletedFiles.add(movie.getThumbnailUrl());
        deletedFiles.add(movie.getPosterUrl());

        List<String> movieItemThumbnails = movieItemRepository.findThumbnailsByMovieId(movie.getId());
        deletedFiles.addAll(movieItemThumbnails);

        // delete media
        mediaService.deleteFiles(deletedFiles);

        // delete comment
        commentRepository.deleteByMovieId(movie.getId());

        // delete favourite
        favouriteRepository.deleteByMovieId(movie.getId());

        // delete movie person
        moviePersonRepository.deleteByMovieId(movie.getId());

        // delete watch history
        watchHistoryRepository.deleteByMovieId(movie.getId());

        // delete sidebar
        sidebarRepository.deleteByMovieId(movie.getId());

        // delete collection item
        collectionItemRepository.deleteByMovieId(movie.getId());

        // delete playlist
        List<Long> playlistIds = playlistRepository.findByMovieId(movie.getId());
        if (!playlistIds.isEmpty()) {
            playlistItemRepository.deleteByMovieId(movie.getId());
            playlistRepository.recalculateTotalMovieByIdIn(playlistIds);
        }

        // delete movie item
        movieItemRepository.deleteByMovieIdAndKind(movie.getId(), BaseConstant.MOVIE_ITEM_KIND_TRAILER);
        movieItemRepository.deleteByMovieIdAndKind(movie.getId(), BaseConstant.MOVIE_ITEM_KIND_EPISODE);
        movieItemRepository.deleteByMovieIdAndKind(movie.getId(), BaseConstant.MOVIE_ITEM_KIND_SEASON);

        // delete movie category
        movie.getCategories().clear();
        movieRepository.save(movie);

        movieRepository.delete(movie);

        redisService.delete(redisService.buildKey(TenantDBContext.getCurrentTenant(), "movie", movie.getId().toString()));
        return makeSuccessResponse("Delete movie success");
    }

    @GetMapping(value = "/suggestion/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<List<MovieDto>> suggestion(@PathVariable("id") Long id) {
        Movie movie = movieRepository.findByIdAndStatus(id, BaseConstant.STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("[Movie] Not found", ErrorCode.MOVIE_ERROR_NOT_FOUND));
        List<Movie> movies = movieRepository.findSuggestion(id,
                movie.getCategories().stream().map(Category::getId).collect(Collectors.toList()),
                movie.getCountry(),
                movie.getLanguage(),
                movie.getType(),
                PageRequest.of(0, 10));
        return makeSuccessResponse(movieMapper.fromEntityToMovieDtoList(movies), "List movie success");
    }

    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<List<WatchHistoryDto>> history() {
        List<WatchHistory> watchHistories = watchHistoryRepository.findLatestInProgressGroupedByMovie(getCurrentUser());
        return makeSuccessResponse(watchHistoryMapper.fromEntityToWatchHistoryDetailsDtoList(watchHistories), "List movie success");
    }

    @GetMapping(value = "/top-views", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<MovieDto>>> topViews(MovieCriteria criteria, Pageable pageable) {
        criteria.setStatus(BaseConstant.STATUS_ACTIVE);
        Page<Movie> movies = movieRepository.findAll(criteria.getSpecification(), pageable);

        return makeSuccessResponse(makeResponseListDto(movies, movieMapper::fromEntityToMovieDtoList), "List movie success");
    }

    @GetMapping(value = "/collection-filter/{collectionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('MOV_L')")
    public ApiMessageDto<ResponseListDto<List<MovieDto>>> collectionFilter(@PathVariable("collectionId") Long collectionId, @RequestParam(value = "title", required = false) String title, Pageable pageable) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new NotFoundException("[Collection] Not found", ErrorCode.COLLECTION_ERROR_NOT_FOUND));
        MovieCriteria criteria = movieMapper.fromFilterMovieFromToMovieCriteria(movieService.parseFilterMovie(collection.getFilter()));
        criteria.setCollectionId(collectionId);
        if (title != null) {
            criteria.setTitle(title);
        }
        Page<Movie> movies = movieRepository.findAll(criteria.getSpecification(), pageable);
        return makeSuccessResponse(makeResponseListDto(movies, movieMapper::fromEntityToMovieDtoList), "List movie success");
    }

    @GetMapping(value = "/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<List<MovieItemDto>> schedule(@RequestParam("date") Date date) {
        Date startOfDay = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
        Date endOfDay = DateUtils.addDays(startOfDay, 1);
        MovieItemCriteria criteria = new MovieItemCriteria();
        criteria.setFromDate(startOfDay);
        criteria.setToDate(endOfDay);
        criteria.setExcludeKind(BaseConstant.MOVIE_ITEM_KIND_TRAILER);
        List<MovieItem> movieItems = movieItemRepository.findAll(criteria.getSpecification());

        return makeSuccessResponse(movieItemMapper.entityToMovieItemDtoWithMovieList(movieItems), "List schedule success");
    }
}
