package com.tenant.api.mapper;

import com.tenant.api.dto.employee.EmployeeDto;
import com.tenant.api.form.employee.CreateEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeProfileForm;
import com.tenant.api.storage.tenant.model.Employee;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToEmployeeDto")
    EmployeeDto entityToEmployeeDto(Employee employee);

    @IterableMapping(elementTargetType = EmployeeDto.class, qualifiedByName = "entityToEmployeeDto")
    List<EmployeeDto> fromEntityToEmployeeDtoList(List<Employee> employees);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToEmployeeDtoProfile")
    EmployeeDto fromEntityToEmployeeDtoProfile(Employee employee);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    Employee fromCreateEmployeeFormToEntity(CreateEmployeeForm form);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateEmployeeFormToEntity(UpdateEmployeeForm form, @MappingTarget Employee employee);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateEmployeeProfileFormToEntity(UpdateEmployeeProfileForm form, @MappingTarget Employee employee);
}
