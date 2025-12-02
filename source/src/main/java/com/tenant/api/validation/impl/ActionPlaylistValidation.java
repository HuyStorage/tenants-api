package com.tenant.api.validation.impl;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.validation.ActionPlaylistConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class ActionPlaylistValidation implements ConstraintValidator<ActionPlaylistConstraint, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(ActionPlaylistConstraint constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null && allowNull) {
            return true;
        }
        return Objects.equals(value, BaseConstant.ACTION_ADD_TO_PLAYLIST)
                || Objects.equals(value, BaseConstant.ACTION_DELETE_FROM_PLAYLIST);
    }
}