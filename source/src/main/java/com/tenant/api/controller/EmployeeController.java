package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.employee.EmployeeDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.form.ChangeStatusForm;
import com.tenant.api.form.employee.CreateEmployeeForm;
import com.tenant.api.form.employee.LoginEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeProfileForm;
import com.tenant.api.mapper.AccountMapper;
import com.tenant.api.mapper.EmployeeMapper;
import com.tenant.api.service.LoginService;
import com.tenant.api.service.MediaService;
import com.tenant.api.storage.tenant.criteria.EmployeeCriteria;
import com.tenant.api.storage.tenant.model.Account;
import com.tenant.api.storage.tenant.model.Employee;
import com.tenant.api.storage.tenant.model.Group;
import com.tenant.api.storage.tenant.repository.AccountRepository;
import com.tenant.api.storage.tenant.repository.EmployeeRepository;
import com.tenant.api.storage.tenant.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/employee")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class EmployeeController extends ABasicController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginService loginService;

    @Autowired
    private MediaService mediaService;

    @Transactional("tenantTransactionManager")
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EM_C')")
    public ApiMessageDto<Void> create(@Valid @RequestBody CreateEmployeeForm form) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed get");
        }

        Account account = accountRepository.findFirstByUsernameAndStatusNot(form.getUsername(), BaseConstant.STATUS_DELETE).orElse(null);
        if (account != null) {
            throw new BadRequestException("[Account] Username existed", ErrorCode.ACCOUNT_ERROR_USERNAME_EXISTED);
        }

        if (StringUtils.isNoneBlank(form.getEmail()) && accountRepository.existsByEmailAndStatusNot(form.getEmail(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[Account] Email is existed", ErrorCode.ACCOUNT_ERROR_EMAIL_EXISTED);
        }

        if (StringUtils.isNoneBlank(form.getPhone()) && accountRepository.existsByPhoneAndStatusNot(form.getPhone(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[Account] Phone is existed", ErrorCode.ACCOUNT_ERROR_PHONE_EXISTED);
        }

        Group group = groupRepository.findByIdAndStatus(form.getGroupId(), BaseConstant.STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("[Group] Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));

        account = accountMapper.fromCreateEmployeeFormToEntity(form);
        account.setPassword(passwordEncoder.encode(form.getPassword()));
        account.setGroup(group);
        account.setKind(BaseConstant.USER_KIND_EMPLOYEE);
        accountRepository.save(account);

        Employee employee = employeeMapper.fromCreateEmployeeFormToEntity(form);
        employee.setAccount(account);
        employeeRepository.save(employee);

        return makeSuccessResponse("Create employee success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EM_V')")
    public ApiMessageDto<EmployeeDto> get(@PathVariable("id") Long id) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed get");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Employee] Not found", ErrorCode.EMPLOYEE_ERROR_NOT_FOUND));

        return makeSuccessResponse(employeeMapper.entityToEmployeeDto(employee), "Get employee success.");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EM_L')")
    public ApiMessageDto<ResponseListDto<List<EmployeeDto>>> list(EmployeeCriteria criteria, Pageable pageable) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed get");
        }

        Page<Employee> employees = employeeRepository.findAll(criteria.getSpecification(), pageable);
        return makeSuccessResponse(makeResponseListDto(employees, employeeMapper::fromEntityToEmployeeDtoList), "List employee success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EM_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateEmployeeForm form) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed get");
        }

        Employee employee = employeeRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Employee] Not found", ErrorCode.EMPLOYEE_ERROR_NOT_FOUND));

        // check password
        if (StringUtils.isNoneBlank(form.getNewPassword()) && StringUtils.isNoneBlank(form.getOldPassword())) {
            if (!passwordEncoder.matches(form.getOldPassword(), employee.getAccount().getPassword())) {
                throw new BadRequestException("[Employee] Wrong password", ErrorCode.EMPLOYEE_ERROR_WRONG_PASSWORD);
            }
            if (form.getNewPassword().equals(form.getOldPassword())) {
                throw new BadRequestException("[Employee] New password must be different from old password", ErrorCode.EMPLOYEE_ERROR_NEW_PASSWORD_SAME_OLD_PASSWORD);
            }
            employee.getAccount().setPassword(passwordEncoder.encode(form.getNewPassword()));
        }

        // check phone
        if (StringUtils.isNotBlank(form.getPhone()) && !Objects.equals(employee.getAccount().getPhone(), form.getPhone())
                && employeeRepository.existsByAccountPhoneAndStatusNot(form.getPhone(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[Employee] Phone existed", ErrorCode.EMPLOYEE_ERROR_PHONE_EXISTED);
        }

        // check email
        if (StringUtils.isNotBlank(form.getEmail()) && !Objects.equals(employee.getAccount().getEmail(), form.getEmail())
                && employeeRepository.existsByAccountEmailAndStatusNot(form.getEmail(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[Employee] Email existed", ErrorCode.EMPLOYEE_ERROR_EMAIL_EXISTED);
        }

        // check username
        if (StringUtils.isNotBlank(form.getUsername()) && !Objects.equals(employee.getAccount().getUsername(), form.getUsername())
                && employeeRepository.existsByAccountUsernameAndStatusNot(form.getUsername(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[Employee] Username existed", ErrorCode.EMPLOYEE_ERROR_USERNAME_EXISTED);
        }

        Group group = groupRepository.findByIdAndStatus(form.getGroupId(), BaseConstant.STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("[Group] Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));

        if (employee.getAccount().getGroup() != null && !Objects.equals(group.getId(), employee.getAccount().getGroup().getId())) {
            employee.getAccount().setGroup(group);
        }

        if (!Objects.equals(form.getAvatarPath(), employee.getAccount().getAvatarPath())) {
            String avatarPath = employee.getAccount().getAvatarPath();
            mediaService.deleteFile(avatarPath);
        }

        accountMapper.fromUpdateEmployeeFormToEntity(form, employee.getAccount());
        accountRepository.save(employee.getAccount());

        employeeMapper.fromUpdateEmployeeFormToEntity(form, employee);
        employeeRepository.save(employee);

        return makeSuccessResponse("Update employee success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/change-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EM_U')")
    public ApiMessageDto<Void> changeStatus(@Valid @RequestBody ChangeStatusForm form) {
        Employee employee = employeeRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[Employee] Not found", ErrorCode.EMPLOYEE_ERROR_NOT_FOUND));
        employee.getAccount().setStatus(form.getStatus());
        accountRepository.save(employee.getAccount());
        employee.setStatus(form.getStatus());
        employeeRepository.save(employee);
        return makeSuccessResponse("Change status success");
    }

    @Transactional("tenantTransactionManager")
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EM_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed get");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Employee] Not found", ErrorCode.EMPLOYEE_ERROR_NOT_FOUND));

        mediaService.deleteFile(employee.getAccount().getAvatarPath());

        employeeRepository.deleteById(id);
        accountRepository.deleteById(id);

        return makeSuccessResponse("Delete employee success");
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public OAuth2AccessToken login(@Valid @RequestBody LoginEmployeeForm form) {
        Employee employee = employeeRepository.findFirstByAccountUsernameAndStatusNot(form.getUsername(), BaseConstant.STATUS_DELETE).orElse(null);
        if (employee == null) {
            log.error("Invalid username or password.");
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        if (!passwordEncoder.matches(form.getPassword(), employee.getAccount().getPassword())) {
            log.error("Invalid username or password.");
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        if (employee.getStatus() != 1) {
            log.error("User had been locked");
            throw new BadRequestException("Account is locked", ErrorCode.ACCOUNT_ERROR_LOOKED);
        }

        OAuth2AccessToken result = loginService.getToken(employee.getAccount(), BaseConstant.LOGIN_ROLE_EMPLOYEE);
        if (result == null) {
            log.error("Get token failed");
        }
        return result;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<EmployeeDto> profile() {
        Employee employee = employeeRepository.findById(getCurrentUser())
                .orElseThrow(() -> new NotFoundException("[Employee] Not found", ErrorCode.EMPLOYEE_ERROR_NOT_FOUND));
        return makeSuccessResponse(employeeMapper.fromEntityToEmployeeDtoProfile(employee), "Get profile success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> updateProfile(@Valid @RequestBody UpdateEmployeeProfileForm form) {
        Employee employee = employeeRepository.findById(getCurrentUser())
                .orElseThrow(() -> new NotFoundException("[Employee] Not found", ErrorCode.EMPLOYEE_ERROR_NOT_FOUND));

        if (StringUtils.isNoneBlank(form.getNewPassword()) && StringUtils.isNoneBlank(form.getOldPassword())) {
            if (!passwordEncoder.matches(form.getOldPassword(), employee.getAccount().getPassword())) {
                throw new BadRequestException("[Employee] Wrong password", ErrorCode.EMPLOYEE_ERROR_WRONG_PASSWORD);
            }
            if (form.getNewPassword().equals(form.getOldPassword())) {
                throw new BadRequestException("[Employee] New password must be different from old password", ErrorCode.EMPLOYEE_ERROR_NEW_PASSWORD_SAME_OLD_PASSWORD);
            }
            employee.getAccount().setPassword(passwordEncoder.encode(form.getNewPassword()));
        }

        if (StringUtils.isNotBlank(form.getPhone()) && !Objects.equals(employee.getAccount().getPhone(), form.getPhone())
                && employeeRepository.existsByAccountPhoneAndStatusNot(form.getPhone(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[Employee] Phone existed", ErrorCode.EMPLOYEE_ERROR_PHONE_EXISTED);
        }

        if (StringUtils.isNotBlank(form.getEmail()) && !Objects.equals(employee.getAccount().getEmail(), form.getEmail())
                && employeeRepository.existsByAccountEmailAndStatusNot(form.getEmail(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[Employee] Email existed", ErrorCode.EMPLOYEE_ERROR_EMAIL_EXISTED);
        }

        if (StringUtils.isNotBlank(form.getUsername()) && !Objects.equals(employee.getAccount().getUsername(), form.getUsername())
                && employeeRepository.existsByAccountUsernameAndStatusNot(form.getUsername(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[Employee] Username existed", ErrorCode.EMPLOYEE_ERROR_USERNAME_EXISTED);
        }

        if (!Objects.equals(form.getAvatarPath(), employee.getAccount().getAvatarPath())) {
            String avatarPath = employee.getAccount().getAvatarPath();
            mediaService.deleteFile(avatarPath);
        }

        accountMapper.fromUpdateEmployeeProfileFormToEntity(form, employee.getAccount());
        accountRepository.save(employee.getAccount());

        return makeSuccessResponse("Update employee profile success");
    }
}
