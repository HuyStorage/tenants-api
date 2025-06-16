package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.dto.ApiMessageDto;
import com.tenant.api.dto.ResponseListDto;
import com.tenant.api.jwt.TenantJwt;
import com.tenant.api.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ABasicController {

    @Autowired
    private UserServiceImpl userService;

    public <T> ApiMessageDto<T> makeResponse(Boolean result, T data, String message, String code) {
        ApiMessageDto<T> apiMessageDto = new ApiMessageDto<>();
        apiMessageDto.setResult(result);
        apiMessageDto.setData(data);
        apiMessageDto.setMessage(message);
        apiMessageDto.setCode(code);
        return apiMessageDto;
    }

    public <T> ApiMessageDto<T> makeSuccessResponse(String message) {
        return makeResponse(true, null, message, null);
    }

    public <T> ApiMessageDto<T> makeSuccessResponse(T data, String message) {
        return makeResponse(true, data, message, null);
    }

    public <T, R> ResponseListDto<R> makeResponseListDto(Page<T> page, Function<List<T>, R> mapper) {
        return new ResponseListDto<>(
                mapper.apply(page.getContent()),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public long getCurrentUser() {
        TenantJwt tenantJwt = userService.getAddInfoFromToken();
        return tenantJwt.getAccountId();
    }

    public TenantJwt getSessionFromToken() {
        return userService.getAddInfoFromToken();
    }

    public boolean isSuperAdmin() {
        TenantJwt tenantJwt = userService.getAddInfoFromToken();
        if (tenantJwt != null) {
            return Objects.equals(tenantJwt.getUserKind(), BaseConstant.USER_KIND_ADMIN) && tenantJwt.getIsSuperAdmin();
        }
        return false;
    }

    public boolean isShop() {
        TenantJwt tenantJwt = userService.getAddInfoFromToken();
        if (tenantJwt != null) {
            return Objects.equals(tenantJwt.getUserKind(), BaseConstant.USER_KIND_MANAGER);
        }
        return false;
    }

    public boolean isEmployee() {
        TenantJwt tenantJwt = userService.getAddInfoFromToken();
        if (tenantJwt != null) {
            return Objects.equals(tenantJwt.getUserKind(), BaseConstant.USER_KIND_EMPLOYEE);
        }
        return false;
    }
}
