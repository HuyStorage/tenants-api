package com.tenant.api.controller;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.service.impl.UserServiceImpl;
import com.tenant.api.jwt.TenantJwt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;


public class ABasicController {

    @Autowired
    private UserServiceImpl userService;

    public long getCurrentUser(){
        TenantJwt winWinJwt = userService.getAddInfoFromToken();
        return winWinJwt.getAccountId();
    }

    public TenantJwt getSessionFromToken(){
        return userService.getAddInfoFromToken();
    }

    public boolean isSuperAdmin(){
        TenantJwt winWinJwt = userService.getAddInfoFromToken();
        if(winWinJwt !=null){
            return Objects.equals(winWinJwt.getUserKind(), BaseConstant.USER_KIND_ADMIN) && winWinJwt.getIsSuperAdmin();
        }
        return false;
    }

    public boolean isShop(){
        TenantJwt winWinJwt = userService.getAddInfoFromToken();
        if(winWinJwt !=null){
            return Objects.equals(winWinJwt.getUserKind(), BaseConstant.USER_KIND_MANAGER);
        }
        return false;
    }
}
