package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.Collection;
import java.util.Optional;

public final class DomainValidator {

    private DomainValidator() {}

    public static boolean noNulo(Object valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilObjeto.esNulo(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.NO_NULO, campo));
            return false;
        }
        return true;
    }

    public static boolean noEnBlanco(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilTexto.esVacioONulo(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.NO_EN_BLANCO, campo));
            return false;
        }
        return true;
    }

    public static boolean longitudMaxima(
            String valor, int max, String campo, String codigoError, ValidationResult resultado) {
        if (UtilTexto.aplicarTrim(valor).length() > max) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.LONGITUD_MAXIMA, campo, max));
            return false;
        }
        return true;
    }

    public static boolean longitudMinima(
            String valor, int min, String campo, String codigoError, ValidationResult resultado) {
        if (UtilTexto.aplicarTrim(valor).length() < min) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.LONGITUD_MINIMA, campo, min));
            return false;
        }
        return true;
    }

    public static boolean correoValido(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilTexto.correoValido(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.CORREO_INVALIDO, campo));
            return false;
        }
        return true;
    }

    public static boolean uuidValido(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilUUID.uuidValido(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.UUID_INVALIDO, campo));
            return false;
        }
        return true;
    }

    public static boolean noVacia(Collection<?> valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilColeccion.esVaciaONula(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.COLECCION_VACIA, campo));
            return false;
        }
        return true;
    }

    public static boolean tamanioMaximo(
            Collection<?> valor, int max, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilObjeto.esNulo(valor) && valor.size() > max) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.TAMANIO_MAXIMO, campo, max));
            return false;
        }
        return true;
    }

    public static boolean sinDuplicados(
            Collection<?> valor, String campo, String codigoError, ValidationResult resultado) {
        Optional<?> duplicado = UtilColeccion.primerDuplicado(valor);
        if (duplicado.isPresent()) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.SIN_DUPLICADOS, campo, duplicado.get()));
            return false;
        }
        return true;
    }
}
