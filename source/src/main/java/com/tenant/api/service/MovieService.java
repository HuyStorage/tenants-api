package com.tenant.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.dto.review.ReviewStatisticsDto;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.movie.FilterMovieForm;
import com.tenant.api.form.movie.MovieMetadataForm;
import com.tenant.api.mapper.MovieItemMapper;
import com.tenant.api.mapper.MovieMapper;
import com.tenant.api.service.redis.RedisService;
import com.tenant.api.storage.tenant.criteria.MovieCriteria;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.model.MovieItem;
import com.tenant.api.storage.tenant.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieItemMapper movieItemMapper;

    /**
     * Calculate reviewCount và averageRating for Movie.
     *
     * @param movieId ID Movie
     * @param rating  rating Review
     * @param action  1 = add, -1 = remove
     */
    public ReviewStatisticsDto calculateReview(Long movieId, int rating, int action) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Movie not found"));

        long oldCount = Optional.ofNullable(movie.getReviewCount()).orElse(0L);
        double oldAverage = Optional.ofNullable(movie.getAverageRating()).orElse(0.0);
        rating = rating * 2;
        long newCount = 0L;
        double newAverage = 0.0;

        if (action == BaseConstant.ACTION_ADD) { // ADD review
            newCount = oldCount + 1;
            newAverage = ((oldAverage * oldCount) + rating) / newCount;
            movie.setReviewCount(newCount);
            movie.setAverageRating(newAverage);

        } else if (action == BaseConstant.ACTION_DELETE && oldCount > 1) { // DELETE review
            newCount = oldCount - 1;
            newAverage = ((oldAverage * oldCount) - rating) / newCount;
            movie.setReviewCount(newCount);
            movie.setAverageRating(newAverage);
        }
        movie.setReviewCount(newCount);
        movie.setAverageRating(newAverage);
        movieRepository.save(movie);

        String key = redisService.buildKey(TenantDBContext.getCurrentTenant(), "movie", movieId.toString());
        log.debug("========> key {}", key);
        MovieDto movieDto = redisService.get(key, MovieDto.class);
        if (movieDto != null) {
            movieDto.setAverageRating(newAverage);
            movieDto.setReviewCount(newCount);
            redisService.put(key, movieDto, 5 * 60);
            log.debug("Updated movie review cache for movieId {}", movieId);
        }

        ReviewStatisticsDto statistics = new ReviewStatisticsDto();
        statistics.setReviewCount(newCount);
        statistics.setAverageRating(newAverage);
        return statistics;
    }

    /**
     * Calculate commentCount for Movie.
     *
     * @param movieId id of Movie
     * @param action  1 = add, -1 = remove
     */
    public void calculateComment(Long movieId, int action) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new NotFoundException("Movie not found"));

        long commentCount = movie.getCommentCount() != null ? movie.getCommentCount() : 0L;
        if (action == BaseConstant.ACTION_ADD) {
            commentCount += 1;
        } else if (action == BaseConstant.ACTION_DELETE) {
            if (commentCount > 0) {
                commentCount -= 1;
            }
        }
        movie.setCommentCount(commentCount);
        movieRepository.save(movie);

        String key = redisService.buildKey(TenantDBContext.getCurrentTenant(), "movie", movie.getId().toString());
        log.debug("========> key {}", key);
        MovieDto movieDto = redisService.get(key, MovieDto.class);
        if (movieDto != null) {
            movieDto.setCommentCount(commentCount);
            redisService.put(key, movieDto, 5 * 60);
            log.debug("Updated movie commentCount cache for movieId {}", movie.getId());
        }
    }

    public FilterMovieForm parseFilterMovie(String filterString) {
        FilterMovieForm filter;
        try {
            filter = objectMapper.readValue(filterString, FilterMovieForm.class);
        } catch (Exception e) {
            log.error("Failed to parse filter JSON for collection: {}", filterString, e);
            filter = new FilterMovieForm();
        }
        return filter;
    }

    public List<Movie> getMovieForCollection(String filterString, List<Long> existedMovieIds) {
        FilterMovieForm filter = parseFilterMovie(filterString);
        MovieCriteria criteria = movieMapper.fromFilterMovieFromToMovieCriteria(filter);
        criteria.setExcludeIds(existedMovieIds);
        criteria.setStatus(BaseConstant.STATUS_ACTIVE);

        int limit = filter.getLimit() == null ? 10 : filter.getLimit();
        Pageable pageable = PageRequest.of(0, limit);

        return movieRepository.findAll(criteria.getSpecification(), pageable).getContent();
    }

    public void updateMetaDataMovie(MovieItem movieItem) {
        Movie movie = movieItem.getMovie();
        try {
            MovieMetadataForm metadata = (movie.getMetadata() != null && !movie.getMetadata().isEmpty())
                    ? objectMapper.readValue(movie.getMetadata(), MovieMetadataForm.class)
                    : new MovieMetadataForm();

            if (Objects.equals(movie.getType(), BaseConstant.MOVIE_TYPE_SINGLE)) {
                if (Objects.equals(movieItem.getKind(), BaseConstant.MOVIE_ITEM_KIND_SEASON)) {
                    metadata.setLatestSeason(movieItemMapper.entityToMovieItemMetadataDto(movieItem));
                }
                if (movieItem.getVideo() != null) {
                    metadata.setDuration(movieItem.getVideo().getDuration());
                }
            } else if (Objects.equals(movieItem.getKind(), BaseConstant.MOVIE_ITEM_KIND_EPISODE)) {
                metadata.setLatestSeason(movieItemMapper.entityToMovieItemMetadataDto(movieItem.getParent()));
                metadata.setLatestEpisode(movieItemMapper.entityToMovieItemMetadataDto(movieItem));
            }
            movie.setMetadata(objectMapper.writeValueAsString(metadata));
            movieRepository.save(movie);
        } catch (Exception ex) {
            log.error("Failed to parse metadata JSON for movie: {}", movie.getId(), ex);
        }
    }

    public void resetMetaDataMovie(Movie movie) {
        if (movie.getMetadata() != null && !movie.getMetadata().isEmpty()) {
            try {
                if (Objects.equals(movie.getType(), BaseConstant.MOVIE_TYPE_SINGLE)) {
                    movie.setMetadata(null);
                } else {
                    MovieMetadataForm metadata = objectMapper.readValue(movie.getMetadata(), MovieMetadataForm.class);
                    metadata.setLatestSeason(null);
                    metadata.setLatestEpisode(null);
                    movie.setMetadata(objectMapper.writeValueAsString(metadata));
                }
                movieRepository.save(movie);
            } catch (Exception ex) {
                log.error("Failed to parse metadata JSON for movie id: {}", movie.getId(), ex);
            }
        }
    }
}
