package com.tenant.api.validation;

import com.tenant.api.validation.impl.EmployeeKindValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmployeeKindValidation.class)
@Documented
public @interface EmployeeKind {
    boolean allowNull() default false;

    String message() default "Employee kind is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
