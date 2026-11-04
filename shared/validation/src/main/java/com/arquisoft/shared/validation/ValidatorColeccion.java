package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.Collection;

public final class ValidatorColeccion {

    private ValidatorColeccion() {}

    public static boolean noVacia(Collection<?> valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilColeccion.esVaciaONula(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.obtener(ValidadorKey.COLECCION_VACIA));
            return false;
        }
        return true;
    }

    public static boolean tamanioMaximo(
            Collection<?> valor, int max, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilObjeto.esNulo(valor) && valor.size() > max) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.TAMANIO_MAXIMO, max));
            return false;
        }
        return true;
    }

    public static boolean sinDuplicados(
            Collection<?> valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilColeccion.primerDuplicado(valor).isPresent()) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.obtener(ValidadorKey.SIN_DUPLICADOS));
            return false;
        }
        return true;
    }
}
