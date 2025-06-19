package com.tenant.api.dto.moviePerson;

import com.tenant.api.dto.ABasicAdminDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.dto.person.PersonDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class MoviePersonDto extends ABasicAdminDto {
    private MovieDto movie;
    private PersonDto person;
    private Integer kind;
    private String characterName;
    private Integer ordering;
}
