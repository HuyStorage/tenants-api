package com.tenant.api.mapper;

import com.tenant.api.dto.movieItem.MovieItemDto;
import com.tenant.api.form.movieItem.CreateMovieItemForm;
import com.tenant.api.form.movieItem.UpdateMovieItemForm;
import com.tenant.api.storage.tenant.model.Movie;
import com.tenant.api.storage.tenant.model.MovieItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {MovieMapper.class, VideoLibraryMapper.class})
public interface MovieItemMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "parent", target = "parent", qualifiedByName = "entityToMovieItemAutoCompleteDto")
    @Mapping(source = "movie", target = "movie", qualifiedByName = "entityToMovieAutoCompleteDto")
    @Mapping(source = "video", target = "video", qualifiedByName = "entityToVideoLibraryAutoCompleteDto")
    @Mapping(source = "releaseDate", target = "releaseDate")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToMovieItemDto")
    MovieItemDto entityToMovieItemDto(MovieItem movieItem);

    @IterableMapping(elementTargetType = MovieItemDto.class, qualifiedByName = "entityToMovieItemDto")
    List<MovieItemDto> fromEntityToMovieItemDtoList(List<MovieItem> movieItems);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "video", target = "video", qualifiedByName = "entityToVideoLibraryAutoCompleteDto")
    @Mapping(source = "releaseDate", target = "releaseDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToMovieItemAutoCompleteDto")
    MovieItemDto entityToMovieItemAutoCompleteDto(MovieItem movieItem);

    @IterableMapping(elementTargetType = MovieItemDto.class, qualifiedByName = "entityToMovieItemAutoCompleteDto")
    List<MovieItemDto> fromEntityToMovieItemAutoCompleteDtoList(List<MovieItem> movieItems);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "movie", target = "movie", qualifiedByName = "entityToMovieShortDto")
    @Mapping(source = "video", target = "video", qualifiedByName = "entityToVideoLibraryAutoCompleteDto")
    @Mapping(source = "releaseDate", target = "releaseDate")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToMovieItemShortDto")
    MovieItemDto entityToMovieItemShortDto(MovieItem movieItem);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "ordering", target = "ordering")
    @Mapping(source = "releaseDate", target = "releaseDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    MovieItem fromCreateMovieItemFormToEntity(CreateMovieItemForm form);

    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "releaseDate", target = "releaseDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateMovieItemFormToEntity(UpdateMovieItemForm form, @MappingTarget MovieItem movieItem);
}
