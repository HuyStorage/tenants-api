package com.tenant.api.mapper;

import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.form.movie.CreateMovieForm;
import com.tenant.api.form.movie.UpdateMovieForm;
import com.tenant.api.storage.tenant.model.Movie;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CategoryMapper.class})
public interface MovieMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "originalTitle", target = "originalTitle")
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "thumbnailUrl", target = "thumbnailUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "posterUrl", target = "posterUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "releaseDate", target = "releaseDate")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "isFeatured", target = "isFeatured")
    @Mapping(source = "language", target = "language")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "ageRating", target = "ageRating")
    @Mapping(source = "viewCount", target = "viewCount")
    @Mapping(source = "categories", target = "categories", qualifiedByName = "fromEntityToCategoryAutoCompleteDtoList")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToMovieDto")
    MovieDto entityToMovieDto(Movie movie);

    @IterableMapping(elementTargetType = MovieDto.class, qualifiedByName = "entityToMovieDto")
    List<MovieDto> fromEntityToMovieDtoList(List<Movie> movies);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "originalTitle", target = "originalTitle")
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "type", target = "type")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToMovieAutoCompleteDto")
    MovieDto entityToMovieAutoCompleteDto(Movie movie);

    @IterableMapping(elementTargetType = MovieDto.class, qualifiedByName = "entityToMovieAutoCompleteDto")
    List<MovieDto> fromEntityToMovieAutoCompleteDtoList(List<Movie> movies);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "originalTitle", target = "originalTitle")
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "thumbnailUrl", target = "thumbnailUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "posterUrl", target = "posterUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "releaseDate", target = "releaseDate")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "isFeatured", target = "isFeatured")
    @Mapping(source = "language", target = "language")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "ageRating", target = "ageRating")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    Movie fromCreateMovieFormToEntity(CreateMovieForm form);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "originalTitle", target = "originalTitle")
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "thumbnailUrl", target = "thumbnailUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "posterUrl", target = "posterUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "releaseDate", target = "releaseDate")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "isFeatured", target = "isFeatured")
    @Mapping(source = "language", target = "language")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "ageRating", target = "ageRating")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateMovieFormToEntity(UpdateMovieForm form, @MappingTarget Movie movie);
}
