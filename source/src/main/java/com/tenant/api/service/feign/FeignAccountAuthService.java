package com.tenant.api.service.feign;


import com.tenant.api.cfg.CustomFeignConfig;
import com.tenant.api.dto.ApiResponse;
import com.tenant.api.dto.account.LoginAuthDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-svr", url = "${auth.internal.base.url}", configuration = CustomFeignConfig.class)
public interface FeignAccountAuthService {
    public static final String LOGIN_TYPE = "BASIC_LOGIN_AUTH";

    @PostMapping(value = "/api/token")
    LoginAuthDto authLogin(@RequestHeader(LOGIN_TYPE) String type, @RequestParam MultiValueMap<String,String> request);

    @GetMapping(value = "/v1/account/get/{id}")
    ApiResponse<Object> authGetAccountById(@PathVariable("id") Long id);

//    @PostMapping(value = "/v1/account/create_admin")
//    ApiResponse<String> authCreateAdmin(@RequestBody CreateAccountAdminForm accountDto);
//
//    @PutMapping(value = "/v1/account/update_admin")
//    ApiResponse<String> authUpdateAdmin(@RequestBody UpdateAccountAdminForm accountDto);

    @GetMapping(value = "/v1/account/delete/{id}")
    ApiResponse<String> authDeleteAccountById(@PathVariable("id") Long id);
}
