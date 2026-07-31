package com.arquisoft.shared.web.validation;

import com.arquisoft.shared.message.AppMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida que un {@code String} tenga formato de identificador único universal (UUID).
 *
 * <p>Los DTOs de entrada reciben los identificadores como {@code String} (pass-through)
 * y esta constraint garantiza el formato con un mensaje claro y acumulable junto al
 * resto de errores del body — en lugar del error genérico de deserialización de Jackson
 * que produce un campo tipado {@code UUID}.</p>
 *
 * <p>{@code null} pasa la validación: la obligatoriedad la decide {@code @NotNull}
 * (convención Jakarta). Aplicable a campos y a elementos de colección:
 * {@code List<@UuidValido String> estudiantes}.</p>
 */
@Documented
@Constraint(validatedBy = UuidValidoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface UuidValido {

    String message() default AppMessages.Http.UUID_FORMATO_INVALIDO_MSG;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
