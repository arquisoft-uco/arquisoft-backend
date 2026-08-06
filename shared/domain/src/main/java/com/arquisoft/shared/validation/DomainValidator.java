package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.message.Messages;
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
                    Messages.formatear(ValidadorKey.NO_NULO, campo));
            return false;
        }
        return true;
    }

    public static boolean noEnBlanco(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilText.isEmptyOrNull(valor)) {
            resultado.agregarError(campo, codigoError,
                    Messages.formatear(ValidadorKey.NO_EN_BLANCO, campo));
            return false;
        }
        return true;
    }

    public static boolean longitudMaxima(
            String valor, int max, String campo, String codigoError, ValidationResult resultado) {
        if (UtilText.applyTrim(valor).length() > max) {
            resultado.agregarError(campo, codigoError,
                    Messages.formatear(ValidadorKey.LONGITUD_MAXIMA, campo, max));
            return false;
        }
        return true;
    }

    public static boolean longitudMinima(
            String valor, int min, String campo, String codigoError, ValidationResult resultado) {
        if (UtilText.applyTrim(valor).length() < min) {
            resultado.agregarError(campo, codigoError,
                    Messages.formatear(ValidadorKey.LONGITUD_MINIMA, campo, min));
            return false;
        }
        return true;
    }

    public static boolean correoValido(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilText.emailStringIsValid(valor)) {
            resultado.agregarError(campo, codigoError,
                    Messages.formatear(ValidadorKey.CORREO_INVALIDO, campo));
            return false;
        }
        return true;
    }

    public static boolean uuidValido(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilUUID.uuidStringIsValid(valor)) {
            resultado.agregarError(campo, codigoError,
                    Messages.formatear(ValidadorKey.UUID_INVALIDO, campo));
            return false;
        }
        return true;
    }

    public static boolean noVacia(Collection<?> valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilCollection.isEmptyOrNull(valor)) {
            resultado.agregarError(campo, codigoError,
                    Messages.formatear(ValidadorKey.COLECCION_VACIA, campo));
            return false;
        }
        return true;
    }

    public static boolean tamanioMaximo(
            Collection<?> valor, int max, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilObject.isNull(valor) && valor.size() > max) {
            resultado.agregarError(campo, codigoError,
                    Messages.formatear(ValidadorKey.TAMANIO_MAXIMO, campo, max));
            return false;
        }
        return true;
    }

    public static boolean sinDuplicados(
            Collection<?> valor, String campo, String codigoError, ValidationResult resultado) {
        Optional<?> duplicado = UtilCollection.firstDuplicate(valor);
        if (duplicado.isPresent()) {
            resultado.agregarError(campo, codigoError,
                    Messages.formatear(ValidadorKey.SIN_DUPLICADOS, campo, duplicado.get()));
            return false;
        }
        return true;
    }
}
