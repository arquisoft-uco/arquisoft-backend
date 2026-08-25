package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilUUID;

public final class ValidatorUUID {

    private ValidatorUUID() {}

    public static boolean uuidValido(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilUUID.uuidValido(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.UUID_INVALIDO, campo));
            return false;
        }
        return true;
    }
}
