package com.tenant.api.validation.impl;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.validation.SnsAppName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class SnsAppNameValidation implements ConstraintValidator<SnsAppName, String> {
    private boolean allowNull;

    @Override
    public void initialize(SnsAppName constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(String appName, ConstraintValidatorContext constraintValidatorContext) {
        if (appName == null && allowNull) {
            return true;
        }

        return Objects.equals(appName, BaseConstant.APP_NAME_CMS)
                || Objects.equals(appName, BaseConstant.APP_NAME_CLIENT);
    }
}
