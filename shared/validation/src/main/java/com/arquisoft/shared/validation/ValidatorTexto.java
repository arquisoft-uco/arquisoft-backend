package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilTexto;

public final class ValidatorTexto {

    private ValidatorTexto() {}

    public static boolean noEnBlanco(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (UtilTexto.esVacioONulo(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.obtener(ValidadorKey.NO_EN_BLANCO));
            return false;
        }
        return true;
    }

    public static boolean correoValido(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilTexto.correoValido(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.obtener(ValidadorKey.CORREO_INVALIDO));
            return false;
        }
        return true;
    }
}
