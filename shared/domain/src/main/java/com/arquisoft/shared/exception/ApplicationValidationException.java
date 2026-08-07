package com.arquisoft.shared.exception;

import com.arquisoft.shared.validation.ValidationResult;

import java.util.stream.Collectors;

public final class ApplicationValidationException extends ApplicationException {

    private final transient ValidationResult validationResult;

    public ApplicationValidationException(ValidationResult validationResult) {
        super(construirMensaje(validationResult), "APPLICATION_VALIDATION_ERROR");
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
