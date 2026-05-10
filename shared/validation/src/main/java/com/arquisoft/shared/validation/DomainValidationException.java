package com.arquisoft.shared.validation;

import com.arquisoft.shared.exceptions.DomainException;
import java.util.stream.Collectors;

/**
 * Excepción lanzada cuando la validación de una entidad de dominio
 * detecta uno o más errores (Notification Pattern).
 *
 * <p>Extiende {@link DomainException} para integrarse con el manejador
 * global de excepciones. Transporta el {@link ValidationResult} completo,
 * permitiendo al caller acceder a todos los errores individuales.</p>
 *
 * <p>Lanzada exclusivamente por {@link ValidationResult#throwIfHasErrors()}.</p>
 */
public class DomainValidationException extends DomainException {

    private final ValidationResult validationResult;

    public DomainValidationException(ValidationResult validationResult) {
        super(buildMessage(validationResult), "DOMAIN_VALIDATION_ERROR");
        this.validationResult = validationResult;
    }

    /**
     * Retorna el resultado de validación con la lista completa de errores.
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }

    private static String buildMessage(ValidationResult result) {
        return result.getErrors().stream()
            .map(e -> "[%s] %s".formatted(e.errorCode(), e.message()))
            .collect(Collectors.joining(" | "));
    }
}
