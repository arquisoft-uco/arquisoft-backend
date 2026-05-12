package com.arquisoft.shared.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un campo o parámetro que no puede ser {@code null}.
 *
 * <p>Documenta la invariante de dominio correspondiente al guard
 * {@code DomainValidator.notNull}. El código de error por defecto puede
 * sobreescribirse por contexto cuando la nomenclatura del bounded context
 * lo requiera.</p>
 *
 * <p>Anotación de dominio puro — no depende de Jakarta Bean Validation.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface NotNull {

    /** Código de error que se registrará en {@code ValidationResult}. */
    String errorCode() default "CAMPO_NULO";
}
