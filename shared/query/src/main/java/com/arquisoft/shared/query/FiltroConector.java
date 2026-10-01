package com.arquisoft.shared.query;

import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.ConsultaKey;
import com.arquisoft.shared.util.UtilTexto;

public enum FiltroConector {

    AND,
    OR;

    public static FiltroConector parse(String valor) {
        if (UtilTexto.esVacioONulo(valor)) {
            throw invalido(valor);
        }
        try {
            return FiltroConector.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw invalido(valor);
        }
    }

    private static FiltroException invalido(String valor) {
        return new FiltroException(
                Mensajes.formatear(ConsultaKey.ERROR_CONECTOR_INVALIDO, valor),
                AppCodes.Consulta.FILTRO_CONECTOR_INVALIDO);
    }
}
