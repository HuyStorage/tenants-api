package com.tenant.api.validation;

import com.tenant.api.validation.impl.AgeRatingValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeRatingValidation.class)
@Documented
public @interface AgeRatingConstraint {
    boolean allowNull() default false;

    String message() default "Age rating is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
