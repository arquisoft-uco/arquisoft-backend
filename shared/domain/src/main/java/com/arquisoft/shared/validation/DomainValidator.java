package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.AppMessages;
import com.arquisoft.shared.util.UtilCollection;
import com.arquisoft.shared.util.UtilObject;
import com.arquisoft.shared.util.UtilText;
import com.arquisoft.shared.util.UtilUUID;

import java.util.Collection;
import java.util.Optional;

public final class DomainValidator {

    private DomainValidator() {}

    public static boolean notNull(Object value, String fieldName, String errorCode, ValidationResult result) {
        if (UtilObject.isNull(value)) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.NOT_NULL.formatted(fieldName));
            return false;
        }
        return true;
    }

    public static boolean notBlank(String value, String fieldName, String errorCode, ValidationResult result) {
        if (UtilText.isEmptyOrNull(value)) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.NOT_BLANK.formatted(fieldName));
            return false;
        }
        return true;
    }

    public static boolean maxLength(String value, int max, String fieldName, String errorCode, ValidationResult result) {
        if (UtilText.applyTrim(value).length() > max) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.MAX_LENGTH.formatted(fieldName, max));
            return false;
        }
        return true;
    }

    public static boolean minLength(String value, int min, String fieldName, String errorCode, ValidationResult result) {
        if (UtilText.applyTrim(value).length() < min) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.MIN_LENGTH.formatted(fieldName, min));
            return false;
        }
        return true;
    }

    public static boolean validEmail(String value, String fieldName, String errorCode, ValidationResult result) {
        if (!UtilText.emailStringIsValid(value)) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.VALID_EMAIL.formatted(fieldName));
            return false;
        }
        return true;
    }

    public static boolean validUUID(String value, String fieldName, String errorCode, ValidationResult result) {
        if (!UtilUUID.uuidStringIsValid(value)) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.VALID_UUID.formatted(fieldName));
            return false;
        }
        return true;
    }

    public static boolean notEmpty(Collection<?> value, String fieldName, String errorCode, ValidationResult result) {
        if (UtilCollection.isEmptyOrNull(value)) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.NOT_EMPTY.formatted(fieldName));
            return false;
        }
        return true;
    }

    public static boolean maxSize(Collection<?> value, int max, String fieldName, String errorCode, ValidationResult result) {
        if (!UtilObject.isNull(value) && value.size() > max) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.MAX_SIZE.formatted(fieldName, max));
            return false;
        }
        return true;
    }

    public static boolean sinDuplicados(Collection<?> value, String fieldName, String errorCode, ValidationResult result) {
        Optional<?> duplicado = UtilCollection.firstDuplicate(value);
        if (duplicado.isPresent()) {
            result.addError(fieldName, errorCode,
                    AppMessages.DomainValidator.SIN_DUPLICADOS.formatted(fieldName, duplicado.get()));
            return false;
        }
        return true;
    }
}
