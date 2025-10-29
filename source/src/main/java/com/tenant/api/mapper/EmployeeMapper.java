package com.tenant.api.mapper;

import com.tenant.api.dto.employee.EmployeeDto;
import com.tenant.api.form.employee.CreateEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeForm;
import com.tenant.api.storage.tenant.model.Account;
import com.tenant.api.storage.tenant.model.Employee;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {GroupMapper.class})
public interface EmployeeMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "account.kind", target = "kind")
    @Mapping(source = "account.username", target = "username")
    @Mapping(source = "account.phone", target = "phone")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "account.fullName", target = "fullName")
    @Mapping(source = "account.avatarPath", target = "avatarPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "account.group", target = "group", qualifiedByName = "fromEntityToGroupDtoAutoComplete")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToEmployeeDto")
    EmployeeDto entityToEmployeeDto(Employee employee);

    @IterableMapping(elementTargetType = EmployeeDto.class, qualifiedByName = "entityToEmployeeDto")
    List<EmployeeDto> fromEntityToEmployeeDtoList(List<Employee> employees);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "account.kind", target = "kind")
    @Mapping(source = "account.username", target = "username")
    @Mapping(source = "account.phone", target = "phone")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "account.fullName", target = "fullName")
    @Mapping(source = "account.avatarPath", target = "avatarPath", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "account.group", target = "group", qualifiedByName = "fromEntityToGroupDto")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToEmployeeDtoProfile")
    EmployeeDto fromEntityToEmployeeDtoProfile(Employee employee);

    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    Employee fromCreateEmployeeFormToEntity(CreateEmployeeForm form);

    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateEmployeeFormToEntity(UpdateEmployeeForm form, @MappingTarget Employee employee);
}
