package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.watchHistory.ListWatchHistoryDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.watchHistory.TrackingWatchHistoryForm;
import com.tenant.api.mapper.WatchHistoryMapper;
import com.tenant.api.storage.tenant.criteria.WatchHistoryCriteria;
import com.tenant.api.storage.tenant.model.*;
import com.tenant.api.storage.tenant.repository.MovieItemRepository;
import com.tenant.api.storage.tenant.repository.UserRepository;
import com.tenant.api.storage.tenant.repository.WatchHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/watch-history")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class WatchHistoryController extends ABasicController {
    @Autowired
    private WatchHistoryRepository watchHistoryRepository;

    @Autowired
    private WatchHistoryMapper watchHistoryMapper;

    @Autowired
    private MovieItemRepository movieItemRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/tracking", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> tracking(@Valid @RequestBody TrackingWatchHistoryForm form) {
        User user = userRepository.findByIdAndStatus(getCurrentUser(), BaseConstant.STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("[User] not found", ErrorCode.USER_ERROR_NOT_FOUND));

        MovieItem movieItem = movieItemRepository.findByIdAndKindNot(form.getMovieItemId(), BaseConstant.MOVIE_ITEM_KIND_TRAILER)
                .orElseThrow(() -> new NotFoundException("[MovieItem] not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND));

        if (movieItem.getVideo() == null) {
            throw new BadRequestException("[Video] not found", ErrorCode.MOVIE_ITEM_ERROR_NOT_FOUND);
        }

        WatchHistory watchHistory = watchHistoryRepository.findByMovieItemIdAndUserId(movieItem.getId(), user.getId()).orElse(null);
        if (watchHistory == null) {
            watchHistory = new WatchHistory();
            watchHistory.setUser(user);
            watchHistory.setMovie(movieItem.getMovie());
            watchHistory.setMovieItem(movieItem);
        }
        watchHistory.setStatus(BaseConstant.STATUS_ACTIVE);

        Long endOfVideo = movieItem.getVideo().getOutroStart() != null
                ? movieItem.getVideo().getOutroStart()
                : movieItem.getVideo().getDuration();

        watchHistory.setLastWatchSeconds(form.getLastWatchSeconds());
        // watch again
        if (watchHistory.getIsCompleted()) {
            if (watchHistory.getLastWatchSeconds() < endOfVideo) {
                watchHistory.setIsCompleted(false);
            }
        } else if (watchHistory.getLastWatchSeconds() >= endOfVideo) { // completed watch
            watchHistory.setIsCompleted(true);
            watchHistory.setTimesWatched(watchHistory.getTimesWatched() + 1);
        }
        watchHistoryRepository.save(watchHistory);

        WatchHistory movieWatchHistory = watchHistoryRepository.findWatchHistoryMovie(movieItem.getMovie().getId(), user.getId()).orElse(null);
        if (movieWatchHistory == null) {
            movieWatchHistory = new WatchHistory();
            movieWatchHistory.setUser(user);
            movieWatchHistory.setMovie(movieItem.getMovie());
        }
        movieWatchHistory.setStatus(BaseConstant.STATUS_ACTIVE);

        boolean isCompletedMovie = checkCompletedMovie(movieWatchHistory);
        if (isCompletedMovie && !movieWatchHistory.getIsCompleted()) {
            movieWatchHistory.setTimesWatched(movieWatchHistory.getTimesWatched() + 1);
        }
        movieWatchHistory.setIsCompleted(isCompletedMovie);
        watchHistoryRepository.save(movieWatchHistory);
        return makeSuccessResponse("Tracking watch history success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ListWatchHistoryDto> list(@Param("movieId") Long movieId) {
        WatchHistoryCriteria criteria = new WatchHistoryCriteria();
        criteria.setUserId(getCurrentUser());
        criteria.setMovieId(movieId);
        criteria.setStatus(BaseConstant.STATUS_ACTIVE);

        List<WatchHistory> watchHistories = watchHistoryRepository.findAll(criteria.getSpecification());

        // check completed movie -> check isCompleted WatchHistory with null movieItem
        // remove this WatchHistory of Movie when response
        boolean isCompletedMovie = false;
        Iterator<WatchHistory> iterator = watchHistories.iterator();
        while (iterator.hasNext()) {
            WatchHistory watchHistory = iterator.next();
            if (watchHistory.getMovieItem() == null) {
                isCompletedMovie = Boolean.TRUE.equals(watchHistory.getIsCompleted());
                iterator.remove();
                break;
            }
        }

        // sort asc by modifiedDate
        watchHistories = watchHistories.stream()
                .sorted(Comparator.comparing(WatchHistory::getModifiedDate).reversed())
                .collect(Collectors.toList());

        ListWatchHistoryDto listWatchHistoryDto = new ListWatchHistoryDto();
        listWatchHistoryDto.setIsCompletedMovie(isCompletedMovie);
        listWatchHistoryDto.setWatchHistories(watchHistoryMapper.fromEntityToWatchHistoryDtoList(watchHistories));
        return makeSuccessResponse(listWatchHistoryDto, "List watch movie success");
    }

    @DeleteMapping(value = "/delete/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> delete(@PathVariable("movieId") Long movieId) {
        watchHistoryRepository.softDeleteByUserIdAndMovieId(BaseConstant.STATUS_DELETE, getCurrentUser(), movieId);
        return makeSuccessResponse("Delete watch history success");
    }

    private boolean checkCompletedMovie(WatchHistory movieWatchHistory) {
        Movie movie = movieWatchHistory.getMovie();
        User user = movieWatchHistory.getUser();
        Integer kind = Objects.equals(movie.getType(), BaseConstant.MOVIE_TYPE_SINGLE)
                ? BaseConstant.MOVIE_ITEM_KIND_SEASON
                : BaseConstant.MOVIE_ITEM_KIND_EPISODE;
        Long targetTotal = movieItemRepository.countByMovieIdAndKind(movie.getId(), kind);
        Long totalCompleted = watchHistoryRepository.countCompletedWatchHistory(movie.getId(), user.getId(), BaseConstant.STATUS_ACTIVE);
        return Objects.equals(totalCompleted, targetTotal);
    }
}
