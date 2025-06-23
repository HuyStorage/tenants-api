package com.tenant.api.controller;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.account.LoginAuthDto;
import com.tenant.api.dto.employee.EmployeeDto;
import com.tenant.api.dto.user.UserDto;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.exception.UnauthorizationException;
import com.tenant.api.form.employee.CreateEmployeeForm;
import com.tenant.api.form.employee.LoginEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeForm;
import com.tenant.api.form.employee.UpdateEmployeeProfileForm;
import com.tenant.api.form.person.LoginUserForm;
import com.tenant.api.form.user.RegisterUserForm;
import com.tenant.api.form.user.UpdateUserForm;
import com.tenant.api.form.user.UpdateUserProfileForm;
import com.tenant.api.mapper.AccountMapper;
import com.tenant.api.mapper.EmployeeMapper;
import com.tenant.api.mapper.UserMapper;
import com.tenant.api.service.feign.FeignAccountAuthService;
import com.tenant.api.service.feign.FeignConst;
import com.tenant.api.storage.tenant.criteria.EmployeeCriteria;
import com.tenant.api.storage.tenant.criteria.UserCriteria;
import com.tenant.api.storage.tenant.model.*;
import com.tenant.api.storage.tenant.repository.AccountRepository;
import com.tenant.api.storage.tenant.repository.EmployeeRepository;
import com.tenant.api.storage.tenant.repository.GroupRepository;
import com.tenant.api.storage.tenant.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class UserController extends ABasicController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FeignAccountAuthService accountAuthService;

    @Value("${auth.internal.user.username}")
    private String username;

    @Value("${auth.internal.user.password}")
    private String password;


    @Transactional("tenantTransactionManager")
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> create(@Valid @RequestBody RegisterUserForm form) {
        Account account = accountRepository.findFirstByUsernameAndStatusNot(form.getUsername(), BaseConstant.STATUS_DELETE).orElse(null);
        if (account != null) {
            throw new BadRequestException("[Account] Username existed", ErrorCode.ACCOUNT_ERROR_USERNAME_EXISTED);
        }
        if (StringUtils.isNoneBlank(form.getEmail()) && accountRepository.existsByEmailAndStatusNot(form.getEmail(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[Account] Email is existed", ErrorCode.ACCOUNT_ERROR_EMAIL_EXISTED);
        }

        Group group = groupRepository.findFirstByKindAndStatus(BaseConstant.USER_KIND_USER, BaseConstant.STATUS_ACTIVE)
                .orElseThrow(() -> new NotFoundException("[Group] Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));

        account = accountMapper.fromRegisterUserFormToEntity(form);
        account.setPassword(passwordEncoder.encode(form.getPassword()));
        account.setGroup(group);
        account.setKind(BaseConstant.USER_KIND_USER);
        accountRepository.save(account);

        User user = new User();
        user.setAccount(account);
        userRepository.save(user);

        return makeSuccessResponse("Register user success");
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USR_V')")
    public ApiMessageDto<UserDto> get(@PathVariable("id") Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[User] Not found", ErrorCode.USER_ERROR_NOT_FOUND));

        return makeSuccessResponse(userMapper.entityToUserDto(user), "Get user success");
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USR_L')")
    public ApiMessageDto<ResponseListDto<List<UserDto>>> list(UserCriteria criteria, Pageable pageable) {
        Page<User> users = userRepository.findAll(criteria.getSpecification(), pageable);

        List<UserDto> userDtoList = userMapper.fromEntityToUserDtoList(users.getContent());

        ResponseListDto<List<UserDto>> responseListObj = new ResponseListDto<>();
        responseListObj.setContent(userDtoList);
        responseListObj.setTotalPages(users.getTotalPages());
        responseListObj.setTotalElements(users.getTotalElements());
        return makeSuccessResponse(responseListObj, "Get list user success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USR_U')")
    public ApiMessageDto<Void> update(@Valid @RequestBody UpdateUserForm form) {
        User user = userRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[User] Not found", ErrorCode.USER_ERROR_NOT_FOUND));

        accountMapper.fromUpdateUserFormToEntity(form, user.getAccount());
        accountRepository.save(user.getAccount());

        userMapper.fromUpdateUserFormToEntity(form, user);
        userRepository.save(user);
        return makeSuccessResponse("Update user success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/active-vip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> activeVIP() {
        User user = userRepository.findById(getCurrentUser())
                .orElseThrow(() -> new NotFoundException("[User] Not found", ErrorCode.USER_ERROR_NOT_FOUND));
        user.getAccount().setKind(BaseConstant.USER_KIND_USER_VIP);
        accountRepository.save(user.getAccount());

        return makeSuccessResponse("Active vip success");
    }

    @Transactional("tenantTransactionManager")
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USR_D')")
    public ApiMessageDto<Void> delete(@PathVariable("id") Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[User] Not found", ErrorCode.USER_ERROR_NOT_FOUND));
        userRepository.deleteById(id);
        accountRepository.deleteById(id);
        return makeSuccessResponse("Delete user success");
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginAuthDto login(@Valid @RequestBody LoginUserForm form) {
        User user = userRepository.findFirstByAccountUsernameAndStatusNot(form.getUsername(), BaseConstant.STATUS_DELETE).orElse(null);
        if (user == null) {
            log.error("Invalid username or password.");
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        if (!passwordEncoder.matches(form.getPassword(), user.getAccount().getPassword())) {
            log.error("Invalid username or password.");
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        if (user.getStatus() != 1) {
            log.error("User had been locked");
            throw new BadRequestException("Account is locked", ErrorCode.ACCOUNT_ERROR_LOOKED);
        }
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("grant_type", "user");
        request.add("username", username);
        request.add("password", password);
        request.add("tenantId", TenantDBContext.getCurrentTenant());
        request.add("userId", user.getId().toString());
        request.add("userKind", String.valueOf(user.getAccount().getKind()));
        LoginAuthDto result = accountAuthService.authLogin(FeignConst.LOGIN_TYPE_INTERNAL, request);
        log.info(result.toString());
        return result;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<UserDto> profile() {
        long id = getCurrentUser();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[User] Not found", ErrorCode.USER_ERROR_NOT_FOUND));
        return makeSuccessResponse(userMapper.fromEntityToUserDtoProfile(user), "Get profile success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> updateProfile(@Valid @RequestBody UpdateUserProfileForm form) {
        User user = userRepository.findById(getCurrentUser())
                .orElseThrow(() -> new NotFoundException("[User] Not found", ErrorCode.USER_ERROR_NOT_FOUND));

        if (StringUtils.isNoneBlank(form.getNewPassword()) && StringUtils.isNoneBlank(form.getOldPassword())) {
            if (!passwordEncoder.matches(form.getOldPassword(), user.getAccount().getPassword())) {
                throw new BadRequestException("[User] Wrong password", ErrorCode.USER_ERROR_WRONG_PASSWORD);
            }
            if (form.getNewPassword().equals(form.getOldPassword())) {
                throw new BadRequestException("[User] New password must be different from old password", ErrorCode.USER_ERROR_NEW_PASSWORD_SAME_OLD_PASSWORD);
            }
            user.getAccount().setPassword(passwordEncoder.encode(form.getNewPassword()));
        }

        if (StringUtils.isNotBlank(form.getPhone()) && !Objects.equals(user.getAccount().getPhone(), form.getPhone())
                && userRepository.existsByAccountPhoneAndStatusNot(form.getPhone(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[User] Phone existed", ErrorCode.USER_ERROR_PHONE_EXISTED);
        }

        if (StringUtils.isNotBlank(form.getUsername()) && !Objects.equals(user.getAccount().getUsername(), form.getUsername())
                && userRepository.existsByAccountUsernameAndStatusNot(form.getUsername(), BaseConstant.STATUS_DELETE)) {
            throw new BadRequestException("[User] Username existed", ErrorCode.USER_ERROR_USERNAME_EXISTED);
        }

        List<String> deleteFiles = new ArrayList<>();
        if (!Objects.equals(form.getAvatarPath(), user.getAccount().getAvatarPath())) {
            String avatarPath = user.getAccount().getAvatarPath();
            deleteFiles.add(avatarPath);
        }
        accountMapper.fromUpdateUserProfileFormToEntity(form, user.getAccount());
        accountRepository.save(user.getAccount());
        if (!deleteFiles.isEmpty()) {
//            baseApiService.deleteFile(new DeleteListFileForm(deleteFiles));
        }
        return makeSuccessResponse("Update user profile success");
    }
}
