package com.arquisoft.shared.exception;

import com.arquisoft.shared.validation.ValidationResult;

import java.util.stream.Collectors;

public final class DomainValidationException extends DomainException {

    private final ValidationResult validationResult;

    public DomainValidationException(ValidationResult validationResult) {
        super(construirMensaje(validationResult), "DOMAIN_VALIDATION_ERROR");
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    private static String construirMensaje(ValidationResult result) {
        return result.getErrores().stream()
            .map(e -> "[%s] %s".formatted(e.codigoError(), e.mensaje()))
            .collect(Collectors.joining(" | "));
    }
}
