package com.arquisoft.shared.query;


public enum FiltroOperador {

    // Operadores de texto
    CONTIENE,
    NO_CONTIENE,
    EMPIEZA_CON,
    TERMINA_CON,

    // Igualdad — aplica a cualquier tipo
    ES,
    NO_ES,

    // Comparación — aplica a tipos comparables: números y fechas
    MAYOR_QUE,
    MENOR_QUE,
    MAYOR_IGUAL_QUE,
    MENOR_IGUAL_QUE,

    // Nulidad — aplica a cualquier tipo; no requieren valor
    ES_NULO,
    NO_ES_NULO;

    public boolean requiereValor() {
        return this != ES_NULO && this != NO_ES_NULO;
    }

    public static FiltroOperador parse(String valor) {
        try {
            return FiltroOperador.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FiltroException(
                    "Operador de filtro inválido: '" + valor +
                    "'. Opciones: CONTIENE, NO_CONTIENE, EMPIEZA_CON, TERMINA_CON, " +
                    "ES, NO_ES, MAYOR_QUE, MENOR_QUE, MAYOR_IGUAL_QUE, MENOR_IGUAL_QUE, ES_NULO, NO_ES_NULO",
                    "FILTRO_OPERADOR_INVALIDO"
            );
        }
    }
}
