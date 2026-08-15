package com.arquisoft.shared.query;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.ConsultaKey;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    private static final String OPCIONES = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.joining(", "));

    public boolean requiereValor() {
        return this != ES_NULO && this != NO_ES_NULO;
    }

    public static FiltroOperador parse(String valor) {
        try {
            return FiltroOperador.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FiltroException(
                    Mensajes.formatear(ConsultaKey.ERROR_OPERADOR_INVALIDO, valor, OPCIONES),
                    AppCodes.Consulta.FILTRO_OPERADOR_INVALIDO
            );
        }
    }
}
