package com.tenant.api.mapper;

import com.tenant.api.dto.moviePerson.MoviePersonDto;
import com.tenant.api.dto.person.PersonDto;
import com.tenant.api.form.group.CreateGroupForm;
import com.tenant.api.form.movie.UpdateMovieForm;
import com.tenant.api.form.moviePerson.CreateMoviePersonForm;
import com.tenant.api.form.moviePerson.UpdateMoviePersonForm;
import com.tenant.api.storage.tenant.model.Group;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.model.MoviePerson;
import com.tenant.api.storage.tenant.model.Person;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {MovieMapper.class, PersonMapper.class})
public interface MoviePersonMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "movie", target = "movie", qualifiedByName = "entityToMovieAutoCompleteDto")
    @Mapping(source = "person", target = "person", qualifiedByName = "entityToPersonAutoCompleteDto")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "characterName", target = "characterName")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToMoviePersonDto")
    MoviePersonDto entityToMoviePersonDto(MoviePerson moviePerson);

    @IterableMapping(elementTargetType = MoviePersonDto.class, qualifiedByName = "entityToMoviePersonDto")
    List<MoviePersonDto> fromEntityToMoviePersonDtoList(List<MoviePerson> moviePersons);

    @Mapping(source = "kind", target = "kind")
    @BeanMapping(ignoreByDefault = true)
    MoviePerson fromCreateMoviePersonFormToEntity(CreateMoviePersonForm form);

}
