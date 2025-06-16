package com.tenant.api.mapper;

import com.tenant.api.dto.groupPermission.GroupPermissionDto;
import com.tenant.api.storage.tenant.model.GroupPermission;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GroupPermissionMapper {
    @Mapping(source = "permissionId", target = "id")
    @Mapping(source = "permissionCode", target = "permissionCode")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToPermissionDto")
    GroupPermissionDto fromEntityToPermissionDto(GroupPermission permission);

    @IterableMapping(elementTargetType = GroupPermissionDto.class, qualifiedByName = "fromEntityToPermissionDto")
    @Named("fromEntityToPermissionDtoList")
    List<GroupPermissionDto> fromEntityToPermissionDtoList(List<GroupPermission> permissions);
}
