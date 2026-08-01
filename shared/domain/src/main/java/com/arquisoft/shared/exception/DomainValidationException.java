package com.arquisoft.shared.exception;

import com.arquisoft.shared.validation.ValidationResult;

import java.util.stream.Collectors;

public final class DomainValidationException extends DomainException {

    private final ValidationResult validationResult;

    public DomainValidationException(ValidationResult validationResult) {
        super(buildMessage(validationResult), "DOMAIN_VALIDATION_ERROR");
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    private static String buildMessage(ValidationResult result) {
        return result.getErrors().stream()
            .map(e -> "[%s] %s".formatted(e.errorCode(), e.message()))
            .collect(Collectors.joining(" | "));
    }
}
