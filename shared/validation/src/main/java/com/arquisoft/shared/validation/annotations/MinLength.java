package com.arquisoft.shared.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un campo o parámetro de texto cuya longitud (sin espacios extremos)
 * debe ser igual o mayor al valor indicado en {@link #value()}.
 *
 * <p>Documenta la invariante de dominio correspondiente al guard
 * {@code DomainValidator.minLength}. La evaluación se realiza sobre
 * {@code value.trim().length()}, consistente con el comportamiento del guard.</p>
 *
 * <p>Anotación de dominio puro — no depende de Jakarta Bean Validation.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface MinLength {

    /** Longitud mínima requerida (inclusive) después de aplicar trim. */
    int value();

    /** Código de error que se registrará en {@code ValidationResult}. */
    String errorCode() default "LONGITUD_MINIMA_REQUERIDA";
}
