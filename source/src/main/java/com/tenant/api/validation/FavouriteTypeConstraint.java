package com.tenant.api.validation;

import com.tenant.api.validation.impl.FavouriteTypeValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FavouriteTypeValidation.class)
@Documented
public @interface FavouriteTypeConstraint {
    boolean allowNull() default false;

    String message() default "Favourite type is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
