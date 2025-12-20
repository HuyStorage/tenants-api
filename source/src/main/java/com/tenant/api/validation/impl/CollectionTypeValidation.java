package com.tenant.api.validation.impl;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.validation.CollectionTypeConstraint;
import com.tenant.api.validation.ReactionTypeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class CollectionTypeValidation implements ConstraintValidator<CollectionTypeConstraint, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(CollectionTypeConstraint constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null && allowNull) {
            return true;
        }
        return Objects.equals(value, BaseConstant.COLLECTION_TYPE_TOPIC)
                || Objects.equals(value, BaseConstant.COLLECTION_TYPE_SECTION);
    }
}