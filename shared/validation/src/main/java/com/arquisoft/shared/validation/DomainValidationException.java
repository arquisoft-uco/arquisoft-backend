package com.arquisoft.shared.validation;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.constant.AppCodes;

public final class DomainValidationException extends DomainException {

    private final ValidationResult validationResult;

    public DomainValidationException(ValidationResult validationResult) {
        super(validationResult.describirErrores(), AppCodes.Validacion.DOMINIO);
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
