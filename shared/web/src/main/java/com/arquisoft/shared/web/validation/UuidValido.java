package com.arquisoft.shared.web.validation;

import com.arquisoft.shared.message.AppMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = UuidValidoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface UuidValido {

    String message() default AppMessages.Http.UUID_FORMATO_INVALIDO_MSG;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
