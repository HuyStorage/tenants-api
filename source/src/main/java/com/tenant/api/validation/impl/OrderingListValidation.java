package com.tenant.api.validation.impl;

import com.tenant.api.form.movieItem.OrderingMovieItemForm;
import com.tenant.api.form.movieItem.UpdateOrderingMovieItemForm;
import com.tenant.api.validation.OrderingListConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderingListValidation implements ConstraintValidator<OrderingListConstraint, UpdateOrderingMovieItemForm> {
    private boolean allowNull;

    @Override
    public void initialize(OrderingListConstraint constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(UpdateOrderingMovieItemForm form, ConstraintValidatorContext constraintValidatorContext) {
        List<OrderingMovieItemForm> items = form.getOrderingMovieItems();
        if (items == null && allowNull) {
            return true;
        }
        assert items != null;
        if (items.isEmpty()) {
            return false;
        }

        List<Integer> orderings = items.stream()
                .map(OrderingMovieItemForm::getOrdering)
                .collect(Collectors.toList());

        if (orderings.stream().anyMatch(o -> o == null || o < 1)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("All orderings must be >= 1")
                    .addConstraintViolation();
            return false;
        }

        Set<Integer> unique = new HashSet<>(orderings);
        int min = Collections.min(unique);
        int max = Collections.max(unique);

        boolean isContinuous = unique.size() == orderings.size() && max - min + 1 == orderings.size() && min == 1;
        if (!isContinuous) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Orderings must be a continuous sequence starting from 1")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}