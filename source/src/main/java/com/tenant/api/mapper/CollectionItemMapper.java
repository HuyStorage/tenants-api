package com.tenant.api.mapper;

import com.tenant.api.dto.collectionItem.CollectionItemDto;
import com.tenant.api.dto.movie.MovieDto;
import com.tenant.api.storage.tenant.model.CollectionItem;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {MovieMapper.class})
public interface CollectionItemMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "collection.id", target = "collectionId")
    @Mapping(source = "movie", target = "movie", qualifiedByName = "entityToMovieAutoCompleteDto")
    @Mapping(source = "ordering", target = "ordering")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToCollectionItemDto")
    CollectionItemDto entityToCollectionItemDto(CollectionItem collectionItem);

    @IterableMapping(elementTargetType = CollectionItemDto.class, qualifiedByName = "entityToCollectionItemDto")
    List<CollectionItemDto> entityToCollectionItemDtoList(List<CollectionItem> collectionItems);

    @Named("collectionItemsToMovieDtos")
    default List<MovieDto> collectionItemsToMovieDtos(List<CollectionItem> collectionItems) {
        if (collectionItems == null || collectionItems.isEmpty()) {
            return new ArrayList<>();
        }

        MovieMapper movieMapper = Mappers.getMapper(MovieMapper.class);

        return collectionItems.stream()
                .filter(item -> item.getMovie() != null) // Filter null movies
                .map(item -> movieMapper.entityToMovieAutoCompleteDto(item.getMovie()))
                .collect(Collectors.toList());
    }
}
