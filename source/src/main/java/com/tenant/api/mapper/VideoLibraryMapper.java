package com.tenant.api.mapper;

import com.tenant.api.dto.video.VideoLibraryDto;
import com.tenant.api.form.video.CreateVideoLibraryForm;
import com.tenant.api.form.video.UpdateVideoLibraryForm;
import com.tenant.api.storage.tenant.model.VideoLibrary;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VideoLibraryMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "content", target = "content", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "relativeContentPath", target = "relativeContentPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "thumbnailUrl", target = "thumbnailUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "introStart", target = "introStart")
    @Mapping(source = "introEnd", target = "introEnd")
    @Mapping(source = "outroStart", target = "outroStart")
    @Mapping(source = "duration", target = "duration")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToVideoLibraryDto")
    VideoLibraryDto entityToVideoLibraryDto(VideoLibrary video);

    @IterableMapping(elementTargetType = VideoLibraryDto.class, qualifiedByName = "entityToVideoLibraryDto")
    List<VideoLibraryDto> fromEntityToVideoLibraryDtoList(List<VideoLibrary> videoLibraries);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "content", target = "content", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "relativeContentPath", target = "relativeContentPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "thumbnailUrl", target = "thumbnailUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "duration", target = "duration")
    @Mapping(source = "introStart", target = "introStart")
    @Mapping(source = "introEnd", target = "introEnd")
    @Mapping(source = "outroStart", target = "outroStart")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToVideoLibraryAutoCompleteDto")
    VideoLibraryDto entityToVideoLibraryAutoCompleteDto(VideoLibrary video);

    @IterableMapping(elementTargetType = VideoLibraryDto.class, qualifiedByName = "entityToVideoLibraryAutoCompleteDto")
    List<VideoLibraryDto> fromEntityToVideoLibraryAutoCompleteDtoList(List<VideoLibrary> videoLibraries);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "content", target = "content", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "thumbnailUrl", target = "thumbnailUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "introStart", target = "introStart")
    @Mapping(source = "introEnd", target = "introEnd")
    @Mapping(source = "outroStart", target = "outroStart")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    VideoLibrary fromCreateVideoLibraryFormToEntity(CreateVideoLibraryForm form);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "shortDescription", target = "shortDescription")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "thumbnailUrl", target = "thumbnailUrl", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "introStart", target = "introStart")
    @Mapping(source = "introEnd", target = "introEnd")
    @Mapping(source = "outroStart", target = "outroStart")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateVideoLibraryFormToEntity(UpdateVideoLibraryForm form, @MappingTarget VideoLibrary videoLibrary);
}
