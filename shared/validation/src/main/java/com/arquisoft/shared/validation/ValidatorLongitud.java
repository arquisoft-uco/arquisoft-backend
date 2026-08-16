package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilTexto;

public final class ValidatorLongitud {

    private ValidatorLongitud() {}

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

    // Un solo error con el rango completo, no dos: al usuario le sirve más "entre 3 y 100"
    // que enterarse de un extremo por petición.
    public static boolean longitudEntre(
            String valor, int min, int max, String campo, String codigoError, ValidationResult resultado) {
        int longitud = UtilTexto.aplicarTrim(valor).length();
        if (longitud < min || longitud > max) {
            resultado.agregarError(campo, codigoError,
                    Mensajes.formatear(ValidadorKey.LONGITUD_ENTRE, campo, min, max));
            return false;
        }
        return true;
    }
}
