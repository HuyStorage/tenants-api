package com.tenant.api.mapper;

import com.tenant.api.dto.account.AccountDto;
import com.tenant.api.form.employee.CreateEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeProfileForm;
import com.tenant.api.form.user.RegisterUserForm;
import com.tenant.api.form.user.UpdateUserForm;
import com.tenant.api.form.user.UpdateUserProfileForm;
import com.tenant.api.storage.tenant.model.Account;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @Mapping(source = "kind", target = "kind")
    @BeanMapping(ignoreByDefault = true)
    @Named("entityToAccountDto")
    AccountDto entityToAccountDto(Account account);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    Account fromCreateEmployeeFormToEntity(CreateEmployeeForm form);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateEmployeeFormToEntity(UpdateEmployeeForm form, @MappingTarget Account account);

    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateEmployeeProfileFormToEntity(UpdateEmployeeProfileForm form, @MappingTarget Account account);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "fullName", target = "fullName")
    @BeanMapping(ignoreByDefault = true)
    Account fromRegisterUserFormToEntity(RegisterUserForm form);

    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateUserFormToEntity(UpdateUserForm form, @MappingTarget Account account);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "avatarPath", target = "avatarPath")
    @BeanMapping(ignoreByDefault = true)
    void fromUpdateUserProfileFormToEntity(UpdateUserProfileForm form, @MappingTarget Account account);
}
