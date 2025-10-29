package com.tenant.api.dto.comment;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.account.AccountDto;
import com.tenant.api.dto.movieItem.MovieItemDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class CommentDto extends ABasicAdminDto {
    private MovieItemDto movieItem;
    private Long movieId;
    private String content;
    private Integer totalLike;
    private Integer totalDislike;
    private Integer totalChildren;
    private Boolean isPinned;
    private CommentDto parent;
    private AccountDto author;
}
