package com.arquisoft.shared.query;


public enum FiltroConector {

    AND,
    OR;

    public static FiltroConector parse(String valor) {
        try {
            return FiltroConector.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FiltroException(
                    "Conector de filtro inválido: '" + valor + "'. Use AND u OR",
                    "FILTRO_CONECTOR_INVALIDO"
            );
        }
    }
}
