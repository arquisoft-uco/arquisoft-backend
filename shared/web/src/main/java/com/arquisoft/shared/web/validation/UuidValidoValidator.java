package com.arquisoft.shared.web.validation;

import com.arquisoft.shared.util.UtilUUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UuidValidoValidator implements ConstraintValidator<UuidValido, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return UtilUUID.uuidStringIsValid(value);
    }
}
