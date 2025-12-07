package com.tenant.api.dto.review;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.user.UserDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ReviewDto extends ABasicAdminDto {
    private UserDto author;
    private Long movieId;
    private Integer rate;
    private String content;
    private Integer totalLike;
    private Integer totalDislike;
    private ReviewStatisticsDto statistics;
}
