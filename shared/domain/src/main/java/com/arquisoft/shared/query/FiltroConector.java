package com.arquisoft.shared.query;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.ConsultaKey;

public enum FiltroConector {

    AND,
    OR;

    public static FiltroConector parse(String valor) {
        try {
            return FiltroConector.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FiltroException(
                    Mensajes.formatear(ConsultaKey.ERROR_CONECTOR_INVALIDO, valor),
                    AppCodes.Consulta.FILTRO_CONECTOR_INVALIDO
            );
        }
    }
}
