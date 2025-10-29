package com.tenant.api.dto.favourite;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.dto.person.PersonDto;
import com.tenant.api.dto.user.UserDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class FavouriteDto extends ABasicAdminDto {
    private UserDto user;
    private Integer type;
    private MovieDto movie;
    private PersonDto person;
}
