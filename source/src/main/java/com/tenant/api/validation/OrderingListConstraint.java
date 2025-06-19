package com.tenant.api.validation;

import com.tenant.api.validation.impl.OrderingListValidation;
import com.tenant.api.validation.impl.UsernameValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrderingListValidation.class)
@Documented
public @interface OrderingListConstraint {
    boolean allowNull() default false;

    String message() default "Ordering must be a continuous sequence starting from 1";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
