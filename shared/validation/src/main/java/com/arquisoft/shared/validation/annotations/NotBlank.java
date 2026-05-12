package com.arquisoft.shared.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un campo o parámetro de texto que no puede ser {@code null},
 * vacío ni compuesto únicamente de espacios en blanco.
 *
 * <p>Documenta la invariante de dominio correspondiente al guard
 * {@code DomainValidator.notBlank}. El código de error por defecto puede
 * sobreescribirse por contexto cuando la nomenclatura del bounded context
 * lo requiera.</p>
 *
 * <p>Anotación de dominio puro — no depende de Jakarta Bean Validation.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface NotBlank {

    /** Código de error que se registrará en {@code ValidationResult}. */
    String errorCode() default "CAMPO_VACIO";
}
