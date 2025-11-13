package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.dto.user.GoogleMobileCallback;
import com.tenant.api.dto.user.GoogleWebCallback;
import com.tenant.api.dto.user.UserDto;
import com.tenant.api.dto.user.UserGoogleInfo;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.exception.NotFoundException;
import com.tenant.api.form.ChangeStatusForm;
import com.tenant.api.form.user.*;
import com.tenant.api.mapper.AccountMapper;
import com.tenant.api.mapper.UserMapper;
import com.tenant.api.service.GoogleService;
import com.tenant.api.service.LoginService;
import com.tenant.api.service.MediaService;
import com.tenant.api.storage.tenant.criteria.UserCriteria;
import com.tenant.api.storage.tenant.model.Account;
import com.tenant.api.storage.tenant.model.User;
import com.tenant.api.storage.tenant.repository.AccountRepository;
import com.tenant.api.storage.tenant.repository.FavouriteRepository;
import com.tenant.api.storage.tenant.repository.UserRepository;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private FavouriteRepository favouriteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginService loginService;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private MediaService mediaService;

    @Transactional("tenantTransactionManager")
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> create(@Valid @RequestBody RegisterUserForm form) {
        Account account = accountRepository.findFirstByEmailAndStatusNot(form.getEmail(), BaseConstant.STATUS_DELETE).orElse(null);
        if (account != null) {
            throw new BadRequestException("[Account] Email existed", ErrorCode.ACCOUNT_ERROR_EMAIL_EXISTED);
        }

        account = accountMapper.fromRegisterUserFormToEntity(form);
        account.setPassword(passwordEncoder.encode(form.getPassword()));
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
        return makeSuccessResponse(makeResponseListDto(users, userMapper::fromEntityToUserDtoList), "Get list user success");
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
    @PutMapping(value = "/change-status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USR_U')")
    public ApiMessageDto<Void> changeStatus(@Valid @RequestBody ChangeStatusForm form) {
        User user = userRepository.findById(form.getId())
                .orElseThrow(() -> new NotFoundException("[User] Not found", ErrorCode.USER_ERROR_NOT_FOUND));

        user.getAccount().setStatus(form.getStatus());
        accountRepository.save(user.getAccount());
        user.setStatus(form.getStatus());
        userRepository.save(user);

        return makeSuccessResponse("Change status success");
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

        favouriteRepository.deleteByUserId(id);
        userRepository.deleteById(id);
        accountRepository.deleteById(id);

        return makeSuccessResponse("Delete user success");
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public OAuth2AccessToken login(@Valid @RequestBody LoginUserForm form) {
        User user = userRepository.findFirstByAccountEmailAndStatusNot(form.getEmail(), BaseConstant.STATUS_DELETE).orElse(null);

        if (user == null) {
            log.error("Invalid email or password.");
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        if (!passwordEncoder.matches(form.getPassword(), user.getAccount().getPassword())) {
            log.error("Invalid username or password.");
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        OAuth2AccessToken result = loginService.getToken(user.getAccount(), BaseConstant.LOGIN_ROLE_USER);
        if (result == null) {
            log.error("Get token failed.");
        }
        return result;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<UserDto> profile() {
        User user = userRepository.findById(getCurrentUser())
                .orElseThrow(() -> new NotFoundException("[User] Not found", ErrorCode.USER_ERROR_NOT_FOUND));
        return makeSuccessResponse(userMapper.fromEntityToUserDtoProfile(user), "Get profile success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> updateProfile(@Valid @RequestBody UpdateUserProfileForm form) {
        User user = userRepository.findById(getCurrentUser())
                .orElseThrow(() -> new NotFoundException("[User] Not found", ErrorCode.USER_ERROR_NOT_FOUND));

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

        userMapper.fromUpdateUserProfileFormToEntity(form, user);
        userRepository.save(user);
        if (!deleteFiles.isEmpty()) {
//            baseApiService.deleteFile(new DeleteListFileForm(deleteFiles));
        }
        return makeSuccessResponse("Update user profile success");
    }

    @Transactional("tenantTransactionManager")
    @PutMapping(value = "/change-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Void> changePassword(@Valid @RequestBody ChangePasswordForm form) {
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

        accountRepository.save(user.getAccount());
        return makeSuccessResponse("Update user profile success");
    }

    @GetMapping(value = "/auth/social-login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> socialLogin(@RequestParam Integer loginType) {
        String redirectUri = googleService.generateAuthUrl();
        return makeSuccessResponse(redirectUri, "Success");
    }

    @Transactional("tenantTransactionManager")
    @PostMapping(value = "/auth/web-callback", produces = MediaType.APPLICATION_JSON_VALUE)
    public OAuth2AccessToken socialWebCallback(@Valid @RequestBody GoogleWebCallback googleCallback) throws IOException {
        UserGoogleInfo userInfo = googleService.getUserInfo(googleCallback.getCode());
        OAuth2AccessToken result = loginService.handleSocialLogin(userInfo);
        log.info(result.toString());

        return result;
    }

    @Transactional("tenantTransactionManager")
    @PostMapping(value = "/auth/mobile-callback", produces = MediaType.APPLICATION_JSON_VALUE)
    public OAuth2AccessToken socialMobileCallback(@Valid @RequestBody GoogleMobileCallback callback) throws IOException {
        UserGoogleInfo userInfo = googleService.verifyIdToken(callback.getIdToken(), callback.getPlatform());
        OAuth2AccessToken result = loginService.handleSocialLogin(userInfo);
        log.info(result.toString());

        return result;
    }
}
