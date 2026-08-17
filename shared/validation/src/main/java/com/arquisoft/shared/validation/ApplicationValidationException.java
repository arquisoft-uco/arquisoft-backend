package com.arquisoft.shared.validation;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.message.constant.AppCodes;

public final class ApplicationValidationException extends ApplicationException {

    private final transient ValidationResult validationResult;

    public ApplicationValidationException(ValidationResult validationResult) {
        super(validationResult.describirErrores(), AppCodes.Validacion.APLICACION);
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
