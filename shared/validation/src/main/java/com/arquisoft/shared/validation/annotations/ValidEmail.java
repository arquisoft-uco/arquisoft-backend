package com.arquisoft.shared.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un campo o parámetro de texto que debe tener formato de correo electrónico válido.
 *
 * <p>Documenta la invariante de dominio correspondiente al guard
 * {@code DomainValidator.validEmail}. No acumula error si el valor es {@code null}
 * — combinar con {@link NotBlank} cuando el campo sea obligatorio.</p>
 *
 * <p>Anotación de dominio puro — no depende de Jakarta Bean Validation.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ValidEmail {

    /** Código de error que se registrará en {@code ValidationResult}. */
    String errorCode() default "EMAIL_INVALIDO";
}
