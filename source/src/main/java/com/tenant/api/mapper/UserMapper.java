package com.tenant.api.mapper;

import com.tenant.api.dto.employee.EmployeeDto;
import com.tenant.api.dto.user.UserDto;
import com.tenant.api.form.employee.CreateEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeForm;
import com.tenant.api.form.user.UpdateUserForm;
import com.tenant.api.storage.tenant.model.Employee;
import com.tenant.api.storage.tenant.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "account.kind", target = "kind")
    @Mapping(source = "account.username", target = "username")
    @Mapping(source = "account.phone", target = "phone")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "account.fullName", target = "fullName")
    @Mapping(source = "account.avatarPath", target = "avatarPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToUserDto")
    UserDto entityToUserDto(User user);

    @IterableMapping(elementTargetType = UserDto.class, qualifiedByName = "entityToUserDto")
    List<UserDto> fromEntityToUserDtoList(List<User> users);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "account.kind", target = "kind")
    @Mapping(source = "account.username", target = "username")
    @Mapping(source = "account.phone", target = "phone")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "account.fullName", target = "fullName")
    @Mapping(source = "account.avatarPath", target = "avatarPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToEmployeeDtoProfile")
    UserDto fromEntityToUserDtoProfile(User user);

    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    Employee fromCreateEmployeeFormToEntity(CreateEmployeeForm form);

    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateUserFormToEntity(UpdateUserForm form, @MappingTarget User user);
}
