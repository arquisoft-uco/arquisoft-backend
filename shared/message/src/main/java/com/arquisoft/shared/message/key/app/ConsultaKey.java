package com.arquisoft.shared.message.key.app;

import com.arquisoft.shared.message.ClaveMensaje;

public enum ConsultaKey implements ClaveMensaje {

    ERROR_CONECTOR_INVALIDO("app.dominio.consulta.error.conector-invalido", 0),
    ERROR_CONECTOR_INVALIDO_DETALLE("app.dominio.consulta.error.conector-invalido-detalle", 1),
    ERROR_OPERADOR_INVALIDO("app.dominio.consulta.error.operador-invalido", 0),
    ERROR_OPERADOR_INVALIDO_DETALLE("app.dominio.consulta.error.operador-invalido-detalle", 2),
    ERROR_CAMPO_ORDEN_NO_PERMITIDO("app.dominio.consulta.error.campo-orden-no-permitido", 0),
    ERROR_CAMPO_ORDEN_NO_PERMITIDO_DETALLE("app.dominio.consulta.error.campo-orden-no-permitido-detalle", 2),
    ERROR_PROFUNDIDAD_FILTRO_EXCEDIDA("app.dominio.consulta.error.profundidad-filtro-excedida", 0),
    ERROR_PROFUNDIDAD_FILTRO_EXCEDIDA_DETALLE("app.dominio.consulta.error.profundidad-filtro-excedida-detalle", 1),
    ERROR_CAMPO_FILTRO_NO_PERMITIDO("app.dominio.consulta.error.campo-filtro-no-permitido", 0),
    ERROR_CAMPO_FILTRO_NO_PERMITIDO_DETALLE("app.dominio.consulta.error.campo-filtro-no-permitido-detalle", 2),
    ERROR_VALOR_REQUERIDO("app.dominio.consulta.error.valor-requerido", 0),
    ERROR_VALOR_REQUERIDO_DETALLE("app.dominio.consulta.error.valor-requerido-detalle", 2),
    ERROR_OPERADOR_REQUIERE_LISTA("app.dominio.consulta.error.operador-requiere-lista", 0),
    ERROR_OPERADOR_REQUIERE_LISTA_DETALLE("app.dominio.consulta.error.operador-requiere-lista-detalle", 2),
    ERROR_OPERADOR_NO_ACEPTA_LISTA("app.dominio.consulta.error.operador-no-acepta-lista", 0),
    ERROR_OPERADOR_NO_ACEPTA_LISTA_DETALLE("app.dominio.consulta.error.operador-no-acepta-lista-detalle", 2),
    ERROR_ORDEN_CAMPO_VACIO("app.dominio.consulta.error.orden-campo-vacio", 0),
    ERROR_ORDEN_CAMPO_VACIO_DETALLE("app.dominio.consulta.error.orden-campo-vacio-detalle", 0),
    ERROR_ORDEN_DIRECCION_INVALIDA("app.dominio.consulta.error.orden-direccion-invalida", 0),
    ERROR_ORDEN_DIRECCION_INVALIDA_DETALLE("app.dominio.consulta.error.orden-direccion-invalida-detalle", 1),

    ERROR_CAMPO_FILTRO_DESCONOCIDO("app.infraestructura.consulta.error.campo-filtro-desconocido", 0),
    ERROR_CAMPO_FILTRO_DESCONOCIDO_DETALLE("app.infraestructura.consulta.error.campo-filtro-desconocido-detalle", 2),
    ERROR_OPERADOR_NO_APLICABLE("app.infraestructura.consulta.error.operador-no-aplicable", 0),
    ERROR_OPERADOR_NO_APLICABLE_DETALLE("app.infraestructura.consulta.error.operador-no-aplicable-detalle", 3),
    ERROR_UUID_INVALIDO("app.infraestructura.consulta.error.uuid-invalido", 0),
    ERROR_UUID_INVALIDO_DETALLE("app.infraestructura.consulta.error.uuid-invalido-detalle", 1),
    ERROR_ENTERO_INVALIDO("app.infraestructura.consulta.error.entero-invalido", 0),
    ERROR_ENTERO_INVALIDO_DETALLE("app.infraestructura.consulta.error.entero-invalido-detalle", 1),
    ERROR_DECIMAL_INVALIDO("app.infraestructura.consulta.error.decimal-invalido", 0),
    ERROR_DECIMAL_INVALIDO_DETALLE("app.infraestructura.consulta.error.decimal-invalido-detalle", 1),
    ERROR_FECHA_INVALIDA("app.infraestructura.consulta.error.fecha-invalida", 0),
    ERROR_FECHA_INVALIDA_DETALLE("app.infraestructura.consulta.error.fecha-invalida-detalle", 1),
    ERROR_FECHA_HORA_INVALIDA("app.infraestructura.consulta.error.fecha-hora-invalida", 0),
    ERROR_FECHA_HORA_INVALIDA_DETALLE("app.infraestructura.consulta.error.fecha-hora-invalida-detalle", 1),
    ERROR_BOOLEANO_INVALIDO("app.infraestructura.consulta.error.booleano-invalido", 0),
    ERROR_BOOLEANO_INVALIDO_DETALLE("app.infraestructura.consulta.error.booleano-invalido-detalle", 1),
    ERROR_RUTA_ORDEN_NO_MAPEADA("app.infraestructura.consulta.error.ruta-orden-no-mapeada", 1),
    ERROR_CONECTOR_REQUERIDO("app.infraestructura.consulta.error.conector-requerido", 0),
    ERROR_CONECTOR_REQUERIDO_DETALLE("app.infraestructura.consulta.error.conector-requerido-detalle", 0),

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
