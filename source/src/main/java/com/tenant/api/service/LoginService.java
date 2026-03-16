package com.tenant.api.service;

import com.tenant.api.cfg.tenants.TenantDBContext;
import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ErrorCode;
import com.tenant.api.dto.user.UserGoogleInfo;
import com.tenant.api.exception.BadRequestException;
import com.tenant.api.service.feign.FeignAccountAuthService;
import com.tenant.api.service.feign.FeignConstant;
import com.tenant.api.storage.tenant.model.Account;
import com.tenant.api.storage.tenant.model.GroupPermission;
import com.tenant.api.storage.tenant.model.User;
import com.tenant.api.storage.tenant.repository.AccountRepository;
import com.tenant.api.storage.tenant.repository.UserRepository;
import com.tenant.api.utils.PasswordUtils;
import com.tenant.api.utils.TemplateUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class LoginService {
    @Autowired
    private FeignAccountAuthService accountAuthService;

    @Autowired
    private CommonAsyncService commonAsyncService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${auth.internal.user.username}")
    private String userInternalUsername;

    @Value("${auth.internal.employee.username}")
    private String employeeInternalUsername;

    @Value("${auth.internal.password}")
    private String password;

    public OAuth2AccessToken getToken(Account account, Integer role) {
        String username = "";
        String grantType = "";
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        if (Objects.equals(role, BaseConstant.LOGIN_ROLE_EMPLOYEE)) {
            username = employeeInternalUsername;
            grantType = BaseConstant.GRANT_TYPE_EMPLOYEE;
            String permissions = account.getGroup().getPermissions().stream()
                    .map(GroupPermission::getPermissionCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(","));
            request.add("permissions", permissions);
        } else if (Objects.equals(role, BaseConstant.LOGIN_ROLE_USER)) {
            username = userInternalUsername;
            grantType = BaseConstant.GRANT_TYPE_USER;
        }
        if (account.getStatus() != 1) {
            log.error("User had been locked");
            throw new BadRequestException("Account is locked", ErrorCode.ACCOUNT_ERROR_LOOKED);
        }
        request.add("grant_type", grantType);
        request.add("username", username);
        request.add("password", password);
        request.add("tenantId", TenantDBContext.getCurrentTenant());
        request.add("userId", account.getId().toString());
        request.add("userKind", String.valueOf(account.getKind()));
        return accountAuthService.authLogin(FeignConstant.LOGIN_TYPE_INTERNAL, request);
    }

    public OAuth2AccessToken handleSocialLogin(UserGoogleInfo userInfo) throws IOException {
        String email = userInfo.getEmail();
        String name = userInfo.getName();
        String picture = userInfo.getPicture();

        Account account = accountRepository.findFirstByEmail(email).orElse(null);
        if (account == null) {
            account = new Account();
            account.setEmail(email);
            String password = PasswordUtils.generateSecureRandomPassword();
            account.setPassword(passwordEncoder.encode(password));
            account.setFullName(name);
            account.setAvatarPath(picture);
            account.setKind(BaseConstant.USER_KIND_USER);
            accountRepository.save(account);
            User user = new User();
            user.setAccount(account);
            userRepository.save(user);
            // Send email
            String htmlContent = TemplateUtils.loadTemplate("register-success.html").replace("${email}", email).replace("${password}", password);
            commonAsyncService.sendEmail(email, htmlContent, "Chào mừng đến MovieHub", true);
        }
        return getToken(account, BaseConstant.LOGIN_ROLE_USER);
    }
}
