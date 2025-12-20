package com.tenant.api.mapper;

import com.tenant.api.dto.playlist.PlaylistDto;
import com.tenant.api.form.playlist.CreatePlaylistForm;
import com.tenant.api.form.playlist.UpdatePlaylistForm;
import com.tenant.api.storage.tenant.model.Playlist;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlaylistMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "totalMovie", target = "totalMovie")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToPlaylistDto")
    PlaylistDto entityToPlaylistDto(Playlist playlist);

    @IterableMapping(elementTargetType = PlaylistDto.class, qualifiedByName = "entityToPlaylistDto")
    List<PlaylistDto> entityToPlaylistDtoList(List<Playlist> playlists);

    @Mapping(source = "name", target = "name")
    @BeanMapping(ignoreByDefault = true)
    Playlist fromCreatePlaylistFormToEntity(CreatePlaylistForm form);

    @Mapping(source = "name", target = "name")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdatePlaylistFormToEntity(UpdatePlaylistForm form, @MappingTarget Playlist playlist);
}
