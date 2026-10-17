package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.LocalDate;

public final class ValidatorFecha {

    private ValidatorFecha() {}

    public static boolean fechaValida(String valor, String campo, String codigoError, ValidationResult resultado) {
        if (!UtilFecha.fechaValida(valor)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.FECHA_INVALIDA, campo));
            return false;
        }
        return true;
    }

    public static boolean posterior(
            LocalDate fecha, LocalDate referencia, String campo, String codigoError, ValidationResult resultado) {
        if (UtilObjeto.noEsNulo(fecha) && UtilObjeto.noEsNulo(referencia) && !fecha.isAfter(referencia)) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.FECHA_POSTERIOR, campo, referencia));
            return false;
        }
        return true;
    }
}
