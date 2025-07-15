package com.tenant.api.validation.impl;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.validation.PlatformConstraint;
import com.tenant.api.validation.StatusConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PlatformValidation implements ConstraintValidator<PlatformConstraint, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(PlatformConstraint constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null && allowNull) {
            return true;
        }
        return Objects.equals(value, BaseConstant.PLATFORM_IOS)
                || Objects.equals(value, BaseConstant.PLATFORM_ANDROID);
    }
}