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

    public static boolean noNulo(Object valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilObject.isNull(valor)) {
            resultado.agregarError(campo, codigoError,
                    AppMessages.DomainValidator.NOT_NULL.formatted(campo));
            return false;
        }
        return true;
    }

    public static boolean noEnBlanco(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilText.isEmptyOrNull(valor)) {
            resultado.agregarError(campo, codigoError,
                    AppMessages.DomainValidator.NOT_BLANK.formatted(campo));
            return false;
        }
        return true;
    }

    public static boolean longitudMaxima(
            String valor, int max, String campo, String codigoError, ValidationResult resultado) {
        if (UtilText.applyTrim(valor).length() > max) {
            resultado.agregarError(campo, codigoError,
                    AppMessages.DomainValidator.MAX_LENGTH.formatted(campo, max));
            return false;
        }
        return true;
    }

    public static boolean longitudMinima(
            String valor, int min, String campo, String codigoError, ValidationResult resultado) {
        if (UtilText.applyTrim(valor).length() < min) {
            resultado.agregarError(campo, codigoError,
                    AppMessages.DomainValidator.MIN_LENGTH.formatted(campo, min));
            return false;
        }
        return true;
    }

    public static boolean correoValido(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilText.emailStringIsValid(valor)) {
            resultado.agregarError(campo, codigoError,
                    AppMessages.DomainValidator.VALID_EMAIL.formatted(campo));
            return false;
        }
        return true;
    }

    public static boolean uuidValido(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilUUID.uuidStringIsValid(valor)) {
            resultado.agregarError(campo, codigoError,
                    AppMessages.DomainValidator.VALID_UUID.formatted(campo));
            return false;
        }
        return true;
    }

    public static boolean noVacia(Collection<?> valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilCollection.isEmptyOrNull(valor)) {
            resultado.agregarError(campo, codigoError,
                    AppMessages.DomainValidator.NOT_EMPTY.formatted(campo));
            return false;
        }
        return true;
    }

    public static boolean tamanioMaximo(
            Collection<?> valor, int max, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilObject.isNull(valor) && valor.size() > max) {
            resultado.agregarError(campo, codigoError,
                    AppMessages.DomainValidator.MAX_SIZE.formatted(campo, max));
            return false;
        }
        return true;
    }

    public static boolean sinDuplicados(
            Collection<?> valor, String campo, String codigoError, ValidationResult resultado) {
        Optional<?> duplicado = UtilCollection.firstDuplicate(valor);
        if (duplicado.isPresent()) {
            resultado.agregarError(campo, codigoError,
                    AppMessages.DomainValidator.SIN_DUPLICADOS.formatted(campo, duplicado.get()));
            return false;
        }
        return true;
    }
}
