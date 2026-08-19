package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultaKey implements ClaveMensaje {

    ERROR_CONECTOR_INVALIDO("app.dominio.consulta.error.conector-invalido", 1),
    ERROR_OPERADOR_INVALIDO("app.dominio.consulta.error.operador-invalido", 2),
    ERROR_CAMPO_ORDEN_NO_PERMITIDO("app.dominio.consulta.error.campo-orden-no-permitido", 2),
    ERROR_PROFUNDIDAD_FILTRO_EXCEDIDA("app.dominio.consulta.error.profundidad-filtro-excedida", 1),
    ERROR_CAMPO_FILTRO_NO_PERMITIDO("app.dominio.consulta.error.campo-filtro-no-permitido", 2),
    ERROR_VALOR_REQUERIDO("app.dominio.consulta.error.valor-requerido", 2),
    ERROR_OPERADOR_REQUIERE_LISTA("app.dominio.consulta.error.operador-requiere-lista", 2),
    ERROR_OPERADOR_NO_ACEPTA_LISTA("app.dominio.consulta.error.operador-no-acepta-lista", 2),
    ERROR_ORDEN_CAMPO_VACIO("app.dominio.consulta.error.orden-campo-vacio", 0),
    ERROR_ORDEN_DIRECCION_INVALIDA("app.dominio.consulta.error.orden-direccion-invalida", 1),

    ERROR_CAMPO_FILTRO_DESCONOCIDO("app.infraestructura.consulta.error.campo-filtro-desconocido", 2),
    ERROR_OPERADOR_NO_APLICABLE("app.infraestructura.consulta.error.operador-no-aplicable", 3),
    ERROR_UUID_INVALIDO("app.infraestructura.consulta.error.uuid-invalido", 1),
    ERROR_ENTERO_INVALIDO("app.infraestructura.consulta.error.entero-invalido", 1),
    ERROR_DECIMAL_INVALIDO("app.infraestructura.consulta.error.decimal-invalido", 1),
    ERROR_FECHA_INVALIDA("app.infraestructura.consulta.error.fecha-invalida", 1),
    ERROR_FECHA_HORA_INVALIDA("app.infraestructura.consulta.error.fecha-hora-invalida", 1),
    ERROR_BOOLEANO_INVALIDO("app.infraestructura.consulta.error.booleano-invalido", 1),
    ERROR_RUTA_ORDEN_NO_MAPEADA("app.infraestructura.consulta.error.ruta-orden-no-mapeada", 1),
    ERROR_CONECTOR_REQUERIDO("app.infraestructura.consulta.error.conector-requerido", 0),

    TIPO_TEXTO("app.infraestructura.consulta.tipo.texto", 0),
    TIPO_UUID("app.infraestructura.consulta.tipo.uuid", 0),
    TIPO_ENTERO("app.infraestructura.consulta.tipo.entero", 0),
    TIPO_DECIMAL("app.infraestructura.consulta.tipo.decimal", 0),
    TIPO_FECHA("app.infraestructura.consulta.tipo.fecha", 0),
    TIPO_FECHA_HORA("app.infraestructura.consulta.tipo.fecha-hora", 0),
    TIPO_BOOLEANO("app.infraestructura.consulta.tipo.booleano", 0);

    private final String clave;
    private final int parametros;

    ConsultaKey(String clave, int parametros) {
        this.clave = clave;
        this.parametros = parametros;
    }

    @Override
    public String clave() {
        return clave;
    }

    @Override
    public int parametros() {
        return parametros;
    }
}
