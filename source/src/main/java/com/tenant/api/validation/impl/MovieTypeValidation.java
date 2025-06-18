package com.tenant.api.validation.impl;

import com.tenant.api.constant.BaseConstant;
import com.tenant.api.validation.MovieTypeConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class MovieTypeValidation implements ConstraintValidator<MovieTypeConstraint, Integer> {
    private boolean allowNull;

    @Override
    public void initialize(MovieTypeConstraint constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null && allowNull) {
            return true;
        }
        return Objects.equals(value, BaseConstant.MOVIE_TYPE_SINGLE)
                || Objects.equals(value, BaseConstant.MOVIE_TYPE_SERIES)
                || Objects.equals(value, BaseConstant.MOVIE_TYPE_TRAILER);
    }
}