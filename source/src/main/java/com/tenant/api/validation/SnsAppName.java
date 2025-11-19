package com.tenant.api.validation;

import com.tenant.api.validation.impl.SnsAppNameValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {SnsAppNameValidation.class})
@Documented
public @interface SnsAppName {
    boolean allowNull() default false;

    String message() default "Invalid app name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
