package com.tenant.api.mapper;

import com.tenant.api.dto.appVersion.AppVersionDto;
import com.tenant.api.form.appVersion.CreateAppVersionForm;
import com.tenant.api.form.appVersion.UpdateAppVersionForm;
import com.tenant.api.storage.tenant.model.AppVersion;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppVersionMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "filePath", target = "filePath")
    @Mapping(source = "forceUpdate", target = "forceUpdate")
    @Mapping(source = "changeLog", target = "changeLog")
    @Mapping(source = "isLatest", target = "isLatest")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToAppVersionDto")
    AppVersionDto entityToAppVersionDto(AppVersion appVersion);

    @IterableMapping(elementTargetType = AppVersionDto.class, qualifiedByName = "entityToAppVersionDto")
    List<AppVersionDto> fromEntityToAppVersionDtoList(List<AppVersion> appVersions);

    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "filePath", target = "filePath")
    @Mapping(source = "forceUpdate", target = "forceUpdate")
    @Mapping(source = "changeLog", target = "changeLog")
    @Mapping(source = "isLatest", target = "isLatest")
    @BeanMapping(ignoreByDefault = true)
    AppVersion fromCreateAppVersionFormToEntity(CreateAppVersionForm form);

    @Mapping(source = "code", target = "code")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "filePath", target = "filePath")
    @Mapping(source = "forceUpdate", target = "forceUpdate")
    @Mapping(source = "changeLog", target = "changeLog")
    @Mapping(source = "isLatest", target = "isLatest")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateAppVersionFormToEntity(UpdateAppVersionForm form, @MappingTarget AppVersion appVersion);
}
