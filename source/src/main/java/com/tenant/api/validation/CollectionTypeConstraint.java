package com.tenant.api.validation;

import com.tenant.api.validation.impl.CollectionTypeValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CollectionTypeValidation.class)
@Documented
public @interface CollectionTypeConstraint {
    boolean allowNull() default false;

    String message() default "Collection type is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
