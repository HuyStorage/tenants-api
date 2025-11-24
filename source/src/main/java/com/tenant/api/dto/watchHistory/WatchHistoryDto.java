package com.tenant.api.dto.watchHistory;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.dto.movieItem.MovieItemDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class WatchHistoryDto extends ABasicAdminDto {
    private Long movieItemId;
    private Long movieId;
    private Long userId;
    private Long lastWatchSeconds;
    private Boolean isCompleted;
    private Integer timesWatched;

    private MovieItemDto movieItem;
    private MovieDto movie;
}
