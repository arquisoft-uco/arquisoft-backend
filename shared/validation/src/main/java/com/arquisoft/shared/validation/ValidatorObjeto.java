package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilObjeto;

public final class ValidatorObjeto {

    private ValidatorObjeto() {}

    public static boolean noNulo(Object valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilObjeto.esNulo(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.obtener(ValidadorKey.NO_NULO));
            return false;
        }
        return true;
    }
}
