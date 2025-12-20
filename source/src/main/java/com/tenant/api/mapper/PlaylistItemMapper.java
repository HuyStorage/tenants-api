package com.tenant.api.mapper;

import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.storage.tenant.model.PlaylistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {MovieMapper.class})
public interface PlaylistItemMapper {
    @Named("playlistItemsToMovieDtos")
    default List<MovieDto> playlistItemsToMovieDtos(List<PlaylistItem> playlistItems) {
        if (playlistItems == null || playlistItems.isEmpty()) {
            return new ArrayList<>();
        }

        MovieMapper movieMapper = Mappers.getMapper(MovieMapper.class);

        return playlistItems.stream()
                .filter(item -> item.getMovie() != null) // Filter null movies
                .map(item -> movieMapper.entityToMovieAutoCompleteDto(item.getMovie()))
                .collect(Collectors.toList());
    }
}
