package com.tenant.api.validation.impl;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.validation.EmployeeKind;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class EmployeeKindValidation implements ConstraintValidator<EmployeeKind, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(EmployeeKind constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null && allowNull) {
            return true;
        }
        return Objects.equals(value, BaseConstant.USER_KIND_EMPLOYEE_EDITOR)
                || Objects.equals(value, BaseConstant.USER_KIND_EMPLOYEE_PUBLISHER);
    }
}