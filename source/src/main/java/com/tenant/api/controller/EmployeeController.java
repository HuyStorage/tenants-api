package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.account.LoginAuthDto;
import com.tenant.api.dto.employee.EmployeeDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.form.employee.CreateEmployeeForm;
import com.tenant.api.form.employee.LoginEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeProfileForm;
import com.tenant.api.mapper.AccountMapper;
import com.tenant.api.mapper.EmployeeMapper;
import com.tenant.api.service.LoginService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
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
        account.setKind(group.getKind());
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
        ApiMessageDto<EmployeeDto> apiMessageDto = new ApiMessageDto<>();
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Employee] Not found", ErrorCode.EMPLOYEE_ERROR_NOT_FOUND));

        apiMessageDto.setData(employeeMapper.entityToEmployeeDto(employee));
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Get employee success.");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EM_L')")
    public ApiMessageDto<ResponseListDto<List<EmployeeDto>>> list(EmployeeCriteria criteria, Pageable pageable) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed get");
        }
        Page<Employee> employees = employeeRepository.findAll(criteria.getSpecification(), pageable);

        List<EmployeeDto> employeeDtoList = employeeMapper.fromEntityToEmployeeDtoList(employees.getContent());

        ResponseListDto<List<EmployeeDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(employeeDtoList);
        responseListObj.setTotalPages(employees.getTotalPages());
        responseListObj.setTotalElements(employees.getTotalElements());
        return makeSuccessResponse(responseListObj, "List employee success");
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

        Group group = groupRepository.findByIdAndStatus(form.getGroupId(), BaseConstant.STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("[Group] Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));
        if (employee.getAccount().getGroup() != null && !Objects.equals(group.getId(), employee.getAccount().getGroup().getId())) {
            employee.getAccount().setGroup(group);
            employee.getAccount().setKind(group.getKind());
        }

        List<String> deleteFiles = new ArrayList<>();
        if (!Objects.equals(form.getAvatarPath(), employee.getAccount().getAvatarPath())) {
            String avatarPath = employee.getAccount().getAvatarPath();
            deleteFiles.add(avatarPath);
        }

        accountMapper.fromUpdateEmployeeFormToEntity(form, employee.getAccount());
        accountRepository.save(employee.getAccount());

        employeeMapper.fromUpdateEmployeeFormToEntity(form, employee);
        employeeRepository.save(employee);
        if (!deleteFiles.isEmpty()) {
//            baseApiService.deleteFile(new DeleteListFileForm(deleteFiles));
        }
        return makeSuccessResponse("Update employee success");
    }

    @Transactional("tenantTransactionManager")
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('EM_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        if (!isShop() && !isSuperAdmin()) {
            throw new UnauthorizationException("Not allowed get");
        }
        employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[Employee] Not found", ErrorCode.EMPLOYEE_ERROR_NOT_FOUND));
        employeeRepository.deleteById(id);
        accountRepository.deleteById(id);
        return makeSuccessResponse("Delete employee success");
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginAuthDto login(@Valid @RequestBody LoginEmployeeForm form) {
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
        LoginAuthDto result = loginService.getToken(employee.getAccount(), BaseConstant.LOGIN_ROLE_EMPLOYEE);
        log.info(result.toString());
        return result;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<EmployeeDto> profile() {
        long id = getCurrentUser();
        Employee employee = employeeRepository.findById(id)
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

        List<String> deleteFiles = new ArrayList<>();
        if (!Objects.equals(form.getAvatarPath(), employee.getAccount().getAvatarPath())) {
            String avatarPath = employee.getAccount().getAvatarPath();
            deleteFiles.add(avatarPath);
        }
        accountMapper.fromUpdateEmployeeProfileFormToEntity(form, employee.getAccount());
        accountRepository.save(employee.getAccount());
        if (!deleteFiles.isEmpty()) {
//            baseApiService.deleteFile(new DeleteListFileForm(deleteFiles));
        }
        return makeSuccessResponse("Update employee profile success");
    }
}
