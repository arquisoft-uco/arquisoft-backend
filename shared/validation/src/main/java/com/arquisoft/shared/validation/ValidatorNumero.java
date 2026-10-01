package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilObjeto;

public final class ValidatorNumero {

    private ValidatorNumero() {}

    public static boolean valorMinimo(
            Number valor, Number min, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilObjeto.esNulo(valor) && valor.doubleValue() < min.doubleValue()) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.VALOR_MINIMO, campo, min));
            return false;
        }
        return true;
    }

    public static boolean valorMaximo(
            Number valor, Number max, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilObjeto.esNulo(valor) && valor.doubleValue() > max.doubleValue()) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.VALOR_MAXIMO, campo, max));
            return false;
        }
        return true;
    }

    public static boolean valorEntre(
            Number valor, Number min, Number max, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilObjeto.esNulo(valor)
                && (valor.doubleValue() < min.doubleValue() || valor.doubleValue() > max.doubleValue())) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.VALOR_ENTRE, campo, min, max));
            return false;
        }
        return true;
    }
}
